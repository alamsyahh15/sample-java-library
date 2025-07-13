/**
 * Exception yang dilempar ketika mencoba meminjam buku yang tidak tersedia
 */
public class BookNotAvailableException extends Exception {
    /**
     * Konstruktor dengan pesan error
     * @param message Pesan yang menjelaskan alasan exception
     */
    public BookNotAvailableException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan error dan penyebab
     * @param message Pesan yang menjelaskan alasan exception
     * @param cause Penyebab exception
     */
    public BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}