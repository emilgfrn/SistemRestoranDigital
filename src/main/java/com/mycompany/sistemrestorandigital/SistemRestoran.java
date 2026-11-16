import java.io.*;
import java.util.*;

public class SistemRestoran implements Serializable {
    private static final long serialVersionUID = 1L;

    private String namaRestoran;
    private HashMap<String, Pelanggan> pelanggan;
    private HashMap<String, Menu> daftarMenu;
    private ArrayList<Transaksi> daftarTransaksi;

    public SistemRestoran(String namaRestoran) {
        this.namaRestoran = namaRestoran;
        this.pelanggan = new HashMap<String, Pelanggan>();
        this.daftarMenu = new HashMap<String, Menu>();
        this.daftarTransaksi = new ArrayList<Transaksi>();
    }

    public void tambahPelanggan(String id, String nama) {
        pelanggan.put(id, new Pelanggan(id, nama));
        System.out.println("Pelanggan berhasil ditambahkan: " + nama);
    }

    public void tambahMenu(String kode, String nama, int stok) {
        daftarMenu.put(kode, new Menu(kode, nama, stok));
        System.out.println("Menu berhasil ditambahkan: " + nama + " (stok: " + stok + ")");
    }

    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(namaFile))) {
            StringBuilder isiFile = new StringBuilder();
            String baris;

            while ((baris = reader.readLine()) != null) {
                isiFile.append(baris.trim());
            }

            String isi = isiFile.toString().trim();

            if (isi.isEmpty()) {
                System.out.println("File transaksi kosong.");
                return;
            }

            String[] dataTransaksi = isi.split("\\|");

            for (int i = 0; i < dataTransaksi.length; i++) {
                String data = dataTransaksi[i].trim();

                if (data.isEmpty()) {
                    continue;
                }

                try {
                    prosesSatuTransaksi(data);
                } catch (FormatTransaksiException
                        | DataTidakDitemukanException
                        | StokTidakCukupException
                        | NumberFormatException e) {
                    System.out.println("[TRANSAKSI " + (i + 1) + " GAGAL] " + e.getMessage());
                }
            }
        }
    }

    private void prosesSatuTransaksi(String data)
        throws FormatTransaksiException,
            DataTidakDitemukanException,
            StokTidakCukupException {
        
        String[] bagian = data.split("\\.");

        if (bagian.length != 3) {
            throw new FormatTransaksiException(
                    "Jumlah bagian data tidak sesuai: " + data);
        }

        String idPelanggan = ambilNilai(bagian[0], "ID");
        String kodeMenu = ambilNilai(bagian[1], "KD");
        String teksJumlah = ambilNilai(bagian[2], "JML");

        int jumlah;
        try {
            jumlah = Integer.parseInt(teksJumlah);
        } catch (NumberFormatException e) {
            throw new FormatTransaksiException(
                    "Jumlah bukan angka yang valid: " + teksJumlah);
        }

        if (jumlah <= 0) {
            throw new FormatTransaksiException(
                    "Jumlah transaksi harus lebih dari 0.");
        }

        Pelanggan pembeli = pelanggan.get(idPelanggan);
        if (pembeli == null) {
            throw new DataTidakDitemukanException(
                    "Pelanggan dengan ID " + idPelanggan + " tidak ditemukan.");
        }

        Menu item = daftarMenu.get(kodeMenu);
        if (item == null) {
            throw new DataTidakDitemukanException(
                    "Menu dengan kode " + kodeMenu + " tidak ditemukan.");
        }
        
        item.kurangiStok(jumlah);

        Transaksi transaksi = new Transaksi(pembeli, item, jumlah);
        daftarTransaksi.add(transaksi);

        System.out.println("[BERHASIL] " + pembeli.getNama() + " membeli " + item.getNama() + " sebanyak " + jumlah + ".");
    }

    private String ambilNilai(String bagian, String kunci)
            throws FormatTransaksiException {
        String[] pasangan = bagian.trim().split(">", 2);

        if (pasangan.length != 2 || !pasangan[0].trim().equals(kunci)
                || pasangan[1].trim().isEmpty()) {
            throw new FormatTransaksiException(
                    "Format " + kunci + " tidak valid pada: " + bagian);
        }

        return pasangan[1].trim();
    }

    public void simpanLaporanAkhir() {
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("sistem_state.ser"))) {
            output.writeObject(this);
            System.out.println("\nSerialization berhasil: sistem_state.ser");
        } catch (IOException e) {
            System.out.println("Gagal melakukan serialization: " + e.getMessage());
        }
        
        try (FileWriter writer = new FileWriter("laporan.txt")) {
            writer.write("DAFTAR AKTOR AKTIF - " + namaRestoran + "\n");
            writer.write("========================================\n");

            for (Pelanggan p : pelanggan.values()) {
                writer.write("ID: " + p.getId() + " | Nama: " + p.getNama() + "\n");
            }

            System.out.println("Laporan berhasil dibuat: laporan.txt");
        } catch (IOException e) {
            System.out.println("Gagal membuat laporan: " + e.getMessage());
        }

        System.out.println("\nJumlah transaksi berhasil: " + daftarTransaksi.size());
        System.out.println("Sisa stok menu:");
        for (Map.Entry<String, Menu> data : daftarMenu.entrySet()) {
            Menu item = data.getValue();
            System.out.println(item.getKode() + " - " + item.getNama() + ": " + item.getStok());
        }
    }
}
