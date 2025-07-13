import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Kelas untuk mengelola transaksi peminjaman dan pengembalian buku di perpustakaan
 */
public class Transaction {
    private Book book;
    private Member member;
    private String date;
    private static Database db = new Database();

    /**
     * Konstruktor untuk membuat transaksi baru
     * @param book Buku yang akan dipinjam/dikembalikan
     * @param member Anggota yang melakukan peminjaman/pengembalian
     * @param date Tanggal transaksi
     */
    public Transaction(Book book, Member member, String date) {
        this.book = book;
        this.member = member;
        this.date = date;
    }

    /**
     * Memproses peminjaman buku oleh anggota
     * - Memeriksa ketersediaan buku
     * - Memeriksa batas peminjaman anggota
     * - Mencatat transaksi peminjaman
     * - Mengupdate status ketersediaan buku
     */
    public void processLoan() {
        try {
            if (!book.isAvailable()) {
                throw new BookNotAvailableException("Book is not available for lending");
            }

            // Check if member has reached their loan limit
            int currentLoans = getCurrentLoans(member.getId());
            if (currentLoans >= member.getLoanLimit()) {
                throw new Exception("Member has reached their loan limit");
            }

            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            try {
                // Insert transaction record
                String insertSql = "INSERT INTO transactions (book_id, member_id, loan_date, due_date) VALUES (?, ?, ?, DATE_ADD(?, INTERVAL ? DAY))";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, book.getId());
                    pstmt.setString(2, member.getId());
                    pstmt.setString(3, date);
                    pstmt.setString(4, date);
                    pstmt.setInt(5, member.getLoanPeriod());
                    pstmt.executeUpdate();
                }

                // Update book availability
                String updateSql = "UPDATE books SET available = false WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, book.getId());
                    pstmt.executeUpdate();
                }

                conn.commit();
                book.setAvailable(false);
                JOptionPane.showMessageDialog(null, "Book successfully loaned");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (BookNotAvailableException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Loan Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error processing loan: " + e.getMessage(), "Loan Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Memproses pengembalian buku oleh anggota
     * - Mencatat tanggal pengembalian
     * - Mengupdate status ketersediaan buku
     */
    public void processReturn() {
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            try {
                // Update transaction record
                String updateTransactionSql = "UPDATE transactions SET return_date = ? WHERE book_id = ? AND member_id = ? AND return_date IS NULL";
                try (PreparedStatement pstmt = conn.prepareStatement(updateTransactionSql)) {
                    pstmt.setString(1, date);
                    pstmt.setString(2, book.getId());
                    pstmt.setString(3, member.getId());
                    int updated = pstmt.executeUpdate();

                    if (updated == 0) {
                        throw new Exception("No active loan found for this book and member");
                    }
                }

                // Update book availability
                String updateBookSql = "UPDATE books SET available = true WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                    pstmt.setString(1, book.getId());
                    pstmt.executeUpdate();
                }

                conn.commit();
                book.setAvailable(true);
                JOptionPane.showMessageDialog(null, "Book successfully returned");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error processing return: " + e.getMessage(), "Return Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan jumlah buku yang sedang dipinjam oleh anggota
     * @param memberId ID anggota yang akan diperiksa
     * @return Jumlah buku yang sedang dipinjam
     * @throws SQLException jika terjadi kesalahan pada database
     */
    private int getCurrentLoans(String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE member_id = ? AND return_date IS NULL";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}