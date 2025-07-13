/**
 * Kelas yang merepresentasikan anggota perpustakaan dengan status staff
 * Mewarisi kelas Member dengan batasan peminjaman khusus
 */
public class Staff extends Member {
    /** Batas maksimal jumlah buku yang dapat dipinjam oleh staff */
    private static final int LOAN_LIMIT = 5;
    /** Durasi maksimal peminjaman buku dalam hari untuk staff */
    private static final int LOAN_PERIOD = 30;

    /**
     * Konstruktor untuk membuat anggota staff baru
     * @param id ID unik staff
     * @param name Nama staff
     */
    public Staff(String id, String name) {
        super(id, name, "Staff");
    }

    /**
     * Mendapatkan batas maksimal peminjaman buku untuk staff
     * @return Jumlah maksimal buku yang dapat dipinjam (5 buku)
     */
    @Override
    public int getLoanLimit() {
        return LOAN_LIMIT;
    }

    /**
     * Mendapatkan durasi peminjaman untuk staff
     * @return Durasi maksimal peminjaman dalam hari (30 hari)
     */
    @Override
    public int getLoanPeriod() {
        return LOAN_PERIOD;
    }
}