/**
 * Kelas yang merepresentasikan buku di perpustakaan
 * Mengimplementasikan interface Loanable untuk mendukung fungsi peminjaman
 */
public class Book implements Loanable {
    private String id;
    private String title;
    private String author;
    private boolean available;

    /**
     * Konstruktor untuk membuat buku baru
     * @param id ID unik buku
     * @param title Judul buku
     * @param author Penulis buku
     */
    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    /**
     * Memeriksa ketersediaan buku untuk dipinjam
     * @return true jika buku tersedia, false jika sedang dipinjam
     */
    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * Mengubah status ketersediaan buku
     * @param status true jika buku tersedia, false jika sedang dipinjam
     */
    @Override
    public void setAvailable(boolean status) {
        this.available = status;
    }

    /**
     * Mendapatkan ID buku
     * @return ID buku
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Mendapatkan judul buku
     * @return Judul buku
     */
    public String getTitle() {
        return title;
    }

    /**
     * Mendapatkan nama penulis buku
     * @return Nama penulis
     */
    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return title + " by " + author + (available ? " (Available)" : " (Not Available)");
    }
}