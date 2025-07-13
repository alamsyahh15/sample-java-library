/**
 * Interface untuk objek yang dapat dipinjam di sistem perpustakaan
 */
public interface Loanable {
    /**
     * Memeriksa apakah objek tersedia untuk dipinjam
     * @return true jika tersedia, false jika sedang dipinjam
     */
    boolean isAvailable();
    /**
     * Mengatur status ketersediaan objek
     * @param status true jika tersedia, false jika sedang dipinjam
     */
    void setAvailable(boolean status);
    /**
     * Mendapatkan ID unik objek
     * @return ID objek
     */
    String getId();
}