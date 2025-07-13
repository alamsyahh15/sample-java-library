/**
 * Kelas yang merepresentasikan anggota perpustakaan dengan status mahasiswa
 * Mewarisi kelas Member dengan batasan peminjaman khusus
 */
public class Student extends Member {
    /** Batas maksimal jumlah buku yang dapat dipinjam oleh mahasiswa */
    private static final int LOAN_LIMIT = 3;
    /** Durasi maksimal peminjaman buku dalam hari untuk mahasiswa */
    private static final int LOAN_PERIOD = 14;

    /**
     * Konstruktor untuk membuat anggota mahasiswa baru
     * @param id ID unik mahasiswa
     * @param name Nama mahasiswa
     */
    public Student(String id, String name) {
        super(id, name, "Student");
    }

    /**
     * Mendapatkan batas maksimal peminjaman buku untuk mahasiswa
     * @return Jumlah maksimal buku yang dapat dipinjam (3 buku)
     */
    @Override
    public int getLoanLimit() {
        return LOAN_LIMIT;
    }

    /**
     * Mendapatkan durasi peminjaman untuk mahasiswa
     * @return Durasi maksimal peminjaman dalam hari (14 hari)
     */
    @Override
    public int getLoanPeriod() {
        return LOAN_PERIOD;
    }
}