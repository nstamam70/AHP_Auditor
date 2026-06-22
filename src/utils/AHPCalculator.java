package utils;

/**
 * Implementasi perhitungan Analytical Hierarchy Process (AHP)
 * Sesuai dengan BAB 4 - Penilaian Kinerja Auditor PT. Adji Manajemen Sertifikasi
 */
public class AHPCalculator {

    // Tabel Random Index (RI) Saaty untuk n = 1 sampai 10
    private static final double[] RI_TABLE = {
        0.00, 0.00, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49
    };

    /**
     * Menghitung jumlah total setiap kolom matriks perbandingan berpasangan
     * (Tabel 4.4 dalam dokumen)
     */
    public static double[] hitungJumlahKolom(double[][] matriks) {
        int n = matriks.length;
        double[] jumlahKolom = new double[n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                jumlahKolom[j] += matriks[i][j];
            }
        }
        return jumlahKolom;
    }

    /**
     * Normalisasi matriks: setiap elemen dibagi jumlah kolomnya
     * (Tabel 4.6 dalam dokumen)
     */
    public static double[][] normalisasi(double[][] matriks) {
        int n = matriks.length;
        double[] jumlahKolom = hitungJumlahKolom(matriks);
        double[][] hasil = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                hasil[i][j] = matriks[i][j] / jumlahKolom[j];
            }
        }
        return hasil;
    }

    /**
     * Menghitung bobot prioritas (rata-rata setiap baris hasil normalisasi)
     * (Tabel 4.7 dalam dokumen)
     */
    public static double[] hitungBobotPrioritas(double[][] matriks) {
        int n = matriks.length;
        double[][] normalized = normalisasi(matriks);
        double[] bobot = new double[n];
        for (int i = 0; i < n; i++) {
            double jumlahBaris = 0;
            for (int j = 0; j < n; j++) {
                jumlahBaris += normalized[i][j];
            }
            bobot[i] = jumlahBaris / n;
        }
        return bobot;
    }

    /**
     * Menghitung Lambda Max (nilai eigen maksimum)
     * λi = (Ax)i / wi, lalu λmax = rata-rata λi
     * (Halaman 72 dalam dokumen)
     */
    public static double hitungLambdaMax(double[][] matriks, double[] bobot) {
        int n = matriks.length;
        double lambdaMax = 0;
        for (int i = 0; i < n; i++) {
            double jumlahBaris = 0;
            for (int j = 0; j < n; j++) {
                jumlahBaris += matriks[i][j] * bobot[j];
            }
            lambdaMax += (jumlahBaris / bobot[i]);
        }
        return lambdaMax / n;
    }

    /**
     * Menghitung Consistency Index (CI)
     * CI = (λmax - n) / (n - 1)
     */
    public static double hitungCI(double lambdaMax, int n) {
        return (lambdaMax - n) / (n - 1);
    }

    /**
     * Menghitung Consistency Ratio (CR)
     * CR = CI / RI
     * CR < 0.10 = konsisten
     */
    public static double hitungCR(double ci, int n) {
        double ri = getRI(n);
        if (ri == 0) return 0;
        return ci / ri;
    }

    /**
     * Mendapatkan nilai Random Index (RI) berdasarkan ukuran matriks
     */
    public static double getRI(int n) {
        if (n < 1 || n > 10) return 1.49;
        return RI_TABLE[n - 1];
    }

    /**
     * Cek apakah matriks konsisten (CR < 0.10)
     */
    public static boolean isKonsisten(double[][] matriks) {
        int n = matriks.length;
        double[] bobot = hitungBobotPrioritas(matriks);
        double lambdaMax = hitungLambdaMax(matriks, bobot);
        double ci = hitungCI(lambdaMax, n);
        double cr = hitungCR(ci, n);
        return cr < 0.10;
    }

    /**
     * Menghitung nilai akhir (prioritas global) setiap alternatif
     * PGi = Σ(Wk × Aik)
     * (Tabel 4.43 dalam dokumen)
     *
     * @param bobotKriteria array bobot prioritas kriteria [nKriteria]
     * @param bobotAlternatif matriks bobot alternatif per kriteria [nKriteria][nAlternatif]
     * @return array nilai akhir setiap alternatif [nAlternatif]
     */
    public static double[] hitungNilaiAkhir(double[] bobotKriteria, double[][] bobotAlternatif) {
        int nKriteria = bobotKriteria.length;
        int nAlternatif = bobotAlternatif[0].length;
        double[] nilaiAkhir = new double[nAlternatif];

        for (int j = 0; j < nAlternatif; j++) {
            for (int k = 0; k < nKriteria; k++) {
                nilaiAkhir[j] += bobotKriteria[k] * bobotAlternatif[k][j];
            }
        }
        return nilaiAkhir;
    }

    /**
     * Mendapatkan urutan perangkingan (indeks alternatif dari terbesar ke terkecil)
     */
    public static int[] getRangking(double[] nilaiAkhir) {
        int n = nilaiAkhir.length;
        int[] rangking = new int[n];
        boolean[] used = new boolean[n];

        for (int rank = 0; rank < n; rank++) {
            double max = -1;
            int maxIdx = -1;
            for (int i = 0; i < n; i++) {
                if (!used[i] && nilaiAkhir[i] > max) {
                    max = nilaiAkhir[i];
                    maxIdx = i;
                }
            }
            rangking[rank] = maxIdx;
            used[maxIdx] = true;
        }
        return rangking;
    }

    /**
     * Hasil perhitungan AHP lengkap untuk satu matriks perbandingan
     */
    public static class HasilAHP {
        public double[][] matriksNormalisasi;
        public double[] bobotPrioritas;
        public double lambdaMax;
        public double ci;
        public double cr;
        public boolean konsisten;

        public HasilAHP(double[][] matriks) {
            int n = matriks.length;
            this.matriksNormalisasi = normalisasi(matriks);
            this.bobotPrioritas = hitungBobotPrioritas(matriks);
            this.lambdaMax = hitungLambdaMax(matriks, this.bobotPrioritas);
            this.ci = hitungCI(this.lambdaMax, n);
            this.cr = hitungCR(this.ci, n);
            this.konsisten = this.cr < 0.10;
        }
    }

    /**
     * Proses perhitungan AHP lengkap: dari matriks perbandingan berpasangan
     * hingga menghasilkan bobot prioritas, CI, CR
     */
    public static HasilAHP proses(double[][] matriks) {
        return new HasilAHP(matriks);
    }
}
