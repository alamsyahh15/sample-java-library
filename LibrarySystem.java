import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Kelas utama sistem perpustakaan yang menginisialisasi aplikasi
 */
public class LibrarySystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LibraryGUI();
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

/**
 * Kelas untuk mengelola koneksi database dan migrasi
 */
class Database {
    private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost/library";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASS = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";
    private static Connection conn = null;

    /**
     * Konstruktor untuk inisialisasi koneksi database dan menjalankan migrasi
     */
    public Database() {
        if (conn == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected to MySQL database");
                
                // Run database migrations
                // String migrationsPath = new File("migrations").getAbsolutePath();
                // DatabaseMigration migration = new DatabaseMigration(conn, migrationsPath);
                // migration.migrate();
                
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC driver not found: " + e.getMessage());
                System.exit(1);
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                System.exit(1);
            } 
        }
    }

    /**
     * Mendapatkan koneksi database yang aktif
     * @return Koneksi database yang aktif
     * @throws SQLException jika terjadi kesalahan koneksi
     */
    public Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Reconnected to database");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC driver not found: " + e.getMessage());
                System.exit(1);
            }
        }
        return conn;
    }

    /**
     * Menutup koneksi database
     */
    public void close() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}

/**
 * Kelas antarmuka grafis untuk sistem perpustakaan
 */
class LibraryGUI extends JFrame {
    private Database db;
    private DefaultComboBoxModel<Book> bookModel;
    private DefaultComboBoxModel<Member> memberModel;

    /**
     * Konstruktor untuk membuat antarmuka grafis perpustakaan
     */
    public LibraryGUI() {
        db = new Database();
        initializeUI();
    }

    /**
     * Menginisialisasi komponen antarmuka pengguna
     */
    private void initializeUI() {
        setTitle("Library Book Lending System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        // Book combo box
        bookModel = new DefaultComboBoxModel<>();
        loadBooks();
        JComboBox<Book> bookCombo = new JComboBox<>(bookModel);
        inputPanel.add(new JLabel("Select Book:"));
        inputPanel.add(bookCombo);

        // Member type radio buttons
        JRadioButton studentRadio = new JRadioButton("Student", true);
        JRadioButton staffRadio = new JRadioButton("Staff");
        ButtonGroup memberTypeGroup = new ButtonGroup();
        memberTypeGroup.add(studentRadio);
        memberTypeGroup.add(staffRadio);
        JPanel radioPanel = new JPanel();
        radioPanel.add(studentRadio);
        radioPanel.add(staffRadio);
        inputPanel.add(new JLabel("Member Type:"));
        inputPanel.add(radioPanel);

        // Member combo box
        memberModel = new DefaultComboBoxModel<>();
        loadMembers();
        JComboBox<Member> memberCombo = new JComboBox<>(memberModel);
        inputPanel.add(new JLabel("Select Member:"));
        inputPanel.add(memberCombo);

        // Date field
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);

        // Buttons
        JButton loanButton = new JButton("Borrow Book");
        JButton returnButton = new JButton("Return Book");
        JButton refreshButton = new JButton("Refresh Data");

        loanButton.addActionListener(e -> {
            Book selectedBook = (Book) bookCombo.getSelectedItem();
            Member selectedMember = (Member) memberCombo.getSelectedItem();
            new Transaction(selectedBook, selectedMember, dateField.getText()).processLoan();
            refreshData();
        });

        returnButton.addActionListener(e -> {
            Book selectedBook = (Book) bookCombo.getSelectedItem();
            Member selectedMember = (Member) memberCombo.getSelectedItem();
            new Transaction(selectedBook, selectedMember, dateField.getText()).processReturn();
            refreshData();
        });

        refreshButton.addActionListener(e -> refreshData());

        add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loanButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Memuat daftar buku dari database ke dalam combo box
     */
    private void loadBooks() {
        bookModel.removeAllElements();
        try (Statement stmt = db.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"));
                book.setAvailable(rs.getBoolean("available"));
                bookModel.addElement(book);
            }
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Memuat daftar anggota dari database ke dalam combo box
     */
    private void loadMembers() {
        memberModel.removeAllElements();
        try (Statement stmt = db.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {

            while (rs.next()) {
                Member member;
                String id = rs.getString("id");
                String name = rs.getString("name");
                String type = rs.getString("type");

                if (type.equals("Staff")) {
                    member = new Staff(id, name);
                } else {
                    member = new Student(id, name);
                }
                memberModel.addElement(member);
            }
        } catch (SQLException e) {
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Memperbarui tampilan data buku dan anggota
     */
    private void refreshData() {
        loadBooks();
        loadMembers();
    }
}