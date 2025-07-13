/**
 * Kelas abstrak yang merepresentasikan anggota perpustakaan
 */
public abstract class Member {
    protected String id;
    protected String name;
    protected String type;

    /**
     * Konstruktor untuk membuat anggota baru
     * @param id ID unik anggota
     * @param name Nama anggota
     * @param type Tipe keanggotaan (Student/Staff)
     */
    public Member(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /**
     * Mendapatkan ID anggota
     * @return ID anggota
     */
    public String getId() {
        return id;
    }

    /**
     * Mendapatkan nama anggota
     * @return Nama anggota
     */
    public String getName() {
        return name;
    }

    /**
     * Mendapatkan tipe keanggotaan
     * @return Tipe anggota (Student/Staff)
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

    /**
     * Mendapatkan batas maksimal peminjaman buku
     * @return Jumlah maksimal buku yang dapat dipinjam
     */
    public abstract int getLoanLimit();
    /**
     * Mendapatkan durasi peminjaman dalam hari
     * @return Jumlah hari maksimal peminjaman
     */
    public abstract int getLoanPeriod();
}