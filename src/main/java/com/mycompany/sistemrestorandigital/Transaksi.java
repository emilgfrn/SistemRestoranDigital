import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private Pelanggan pelanggan;
    private Menu menu;
    private int jumlah;
    private String waktu;
    public Transaksi(Pelanggan pelanggan, Menu menu, int jumlah) {
        this.pelanggan = pelanggan;
        this.menu = menu;
        this.jumlah = jumlah;
        this.waktu = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getWaktu() {
        return waktu;
    }
}
