import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas untuk mengelola migrasi skema database
 */
public class DatabaseMigration {
    private static final String MIGRATION_TABLE = "schema_version";
    private final Connection connection;
    private final String migrationsPath;

    /**
     * Konstruktor untuk membuat instance DatabaseMigration
     * @param connection Koneksi database yang aktif
     * @param migrationsPath Path ke direktori file migrasi
     */
    public DatabaseMigration(Connection connection, String migrationsPath) {
        this.connection = connection;
        this.migrationsPath = migrationsPath;
    }

    /**
     * Menjalankan semua file migrasi yang belum diaplikasikan
     * @throws SQLException jika terjadi kesalahan pada database
     * @throws IOException jika terjadi kesalahan membaca file
     */
    public void migrate() throws SQLException, IOException {
        createVersioningTable();
        File migrationsDir = new File(migrationsPath);
        File[] migrationFiles = migrationsDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (migrationFiles != null) {
            for (File migrationFile : migrationFiles) {
                String version = extractVersion(migrationFile.getName());
                if (!isMigrationApplied(version)) {
                    applyMigration(version, migrationFile);
                }
            }
        }
    }

    /**
     * Membuat tabel untuk mencatat versi skema database
     * @throws SQLException jika terjadi kesalahan pada database
     */
    private void createVersioningTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS " + MIGRATION_TABLE + " (" +
                "version VARCHAR(50) PRIMARY KEY," +
                "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"    
            );
        }
    }

    /**
     * Memeriksa apakah versi migrasi sudah diaplikasikan
     * @param version Nomor versi migrasi
     * @return true jika migrasi sudah diaplikasikan
     * @throws SQLException jika terjadi kesalahan pada database
     */
    private boolean isMigrationApplied(String version) throws SQLException {
        String sql = "SELECT version FROM " + MIGRATION_TABLE + " WHERE version = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Mengaplikasikan file migrasi ke database
     * @param version Nomor versi migrasi
     * @param migrationFile File SQL migrasi yang akan dijalankan
     * @throws SQLException jika terjadi kesalahan pada database
     * @throws IOException jika terjadi kesalahan membaca file
     */
    private void applyMigration(String version, File migrationFile) throws SQLException, IOException {
        String[] sqlStatements = readFile(migrationFile).split(";");
        connection.setAutoCommit(false);
        try {
            // Execute each SQL statement separately
            try (Statement stmt = connection.createStatement()) {
                for (String sql : sqlStatements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                        System.out.println("Executed SQL: " + sql);
                    }
                }
            }

            // Record migration
            String insertSql = "INSERT INTO " + MIGRATION_TABLE + " (version) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setString(1, version);
                stmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Applied migration: " + version);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Mengekstrak nomor versi dari nama file migrasi
     * @param filename Nama file migrasi
     * @return Nomor versi migrasi
     */
    private String extractVersion(String filename) {
        return filename.substring(1, filename.indexOf('_'));
    }

    /**
     * Membaca isi file migrasi dan mengabaikan komentar
     * @param file File migrasi yang akan dibaca
     * @return Isi file dalam bentuk string
     * @throws IOException jika terjadi kesalahan membaca file
     */
    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("--")) {
                    content.append(line).append('\n');
                }
            }
        }
        return content.toString();
    }
}