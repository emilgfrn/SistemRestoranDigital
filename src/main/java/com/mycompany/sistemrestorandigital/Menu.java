import java.io.Serializable;

public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    private String kode;
    private String nama;
    private int stok;

    public Menu(String kode, String nama, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getStok() {
        return stok;
    }

    public void kurangiStok(int jumlah) throws StokTidakCukupException {
        if (jumlah <= 0) {
            throw new StokTidakCukupException("Jumlah transaksi harus lebih dari 0.");
        }

        if (jumlah > stok) {
            throw new StokTidakCukupException(
                "Stok menu " + kode + " tidak cukup. Stok tersedia: " + stok + ", diminta: " + jumlah + ".");
        }

        stok -= jumlah;
    }
}
