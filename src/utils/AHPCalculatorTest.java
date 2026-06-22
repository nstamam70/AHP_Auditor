package utils;

/**
 * Self-check: verifikasi perhitungan AHP sesuai BAB 4
 * Jalankan: java utils.AHPCalculatorTest
 */
public class AHPCalculatorTest {

    public static void main(String[] args) {
        System.out.println("=== Verifikasi AHP Calculator vs BAB 4 ===\n");

        // Matriks Perbandingan Berpasangan Kriteria (Tabel 4.3)
        double[][] matriksKriteria = {
            {1,     3,     5,     0.5,   2,     4,     5,     7},     // C1
            {1.0/3, 1,     3,     0.25,  0.5,   2,     3,     5},     // C2
            {0.2,   1.0/3, 1,     0.2,   1.0/3, 1,     2,     3},     // C3
            {2,     4,     5,     1,     3,     5,     6,     7},     // C4
            {0.5,   2,     3,     1.0/3, 1,     3,     4,     5},     // C5
            {0.25,  0.5,   1,     0.2,   1.0/3, 1,     2,     3},     // C6
            {0.2,   1.0/3, 0.5,   1.0/6, 0.25,  0.5,   1,     2},     // C7
            {1.0/7, 0.2,   1.0/3, 1.0/7, 0.2,   1.0/3, 0.5,   1}      // C8
        };

        AHPCalculator.HasilAHP hasil = AHPCalculator.proses(matriksKriteria);

        // Cetak jumlah kolom (harusnya: 4.626, 11.366, 18.833, 2.793, 7.616, 16.833, 23.500, 33.000)
        System.out.println("Jumlah Kolom:");
        double[] jmlKolom = AHPCalculator.hitungJumlahKolom(matriksKriteria);
        String[] namaKriteria = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8"};
        for (int i = 0; i < 8; i++) {
            System.out.printf("  %s: %.3f\n", namaKriteria[i], jmlKolom[i]);
        }

        // Cetak bobot prioritas (harusnya: 0.231, 0.109, 0.060, 0.321, 0.149, 0.063, 0.041, 0.027)
        System.out.println("\nBobot Prioritas Kriteria (Tabel 4.7):");
        for (int i = 0; i < 8; i++) {
            System.out.printf("  %s: %.3f\n", namaKriteria[i], hasil.bobotPrioritas[i]);
        }

        // Cetak konsistensi
        System.out.printf("\nLambda Max: %.3f (expected ~8.260)\n", hasil.lambdaMax);
        System.out.printf("CI: %.3f (expected ~0.037)\n", hasil.ci);
        System.out.printf("CR: %.3f (expected ~0.026)\n", hasil.cr);
        System.out.printf("Konsisten: %s (expected: true)\n", hasil.konsisten);

        // === Test Alternatif C1 (Tabel 4.9) ===
        System.out.println("\n=== Alternatif C1: Kompetensi Auditor ===");
        double[][] matriksC1 = {
            {1,     1.0/3, 3,     0.5,   4},
            {3,     1,     5,     2,     7},
            {1.0/3, 0.2,   1,     1.0/3, 3},
            {2,     0.5,   3,     1,     5},
            {0.25,  1.0/7, 1.0/3, 0.2,   1}
        };
        double[] bobotC1 = AHPCalculator.hitungBobotPrioritas(matriksC1);
        String[] namaAuditor = {"A1-Putra", "A2-Mia", "A3-Agung", "A4-Andre", "A5-Dinda"};
        System.out.println("Bobot (expected: 0.174, 0.433, 0.091, 0.255, 0.046):");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  %s: %.3f\n", namaAuditor[i], bobotC1[i]);
        }

        // === Test Nilai Akhir ===
        System.out.println("\n=== Nilai Akhir (Prioritas Global) ===");

        // Matriks alternatif untuk setiap kriteria (Tabel 4.18, 4.22, 4.26, 4.30, 4.34, 4.38)
        double[][] matriksC2 = {
            {1, 0.5, 3, 2, 4}, {2, 1, 4, 3, 5}, {1.0/3, 0.25, 1, 0.5, 3},
            {0.5, 1.0/3, 2, 1, 4}, {0.25, 0.2, 1.0/3, 0.25, 1}
        };
        double[][] matriksC3 = {
            {1, 0.5, 2, 1.0/3, 4}, {2, 1, 3, 0.5, 5}, {0.5, 1.0/3, 1, 0.25, 3},
            {3, 2, 4, 1, 6}, {0.25, 0.2, 1.0/3, 1.0/6, 1}
        };
        double[][] matriksC4 = {
            {1, 0.5, 3, 2, 4}, {2, 1, 4, 3, 5}, {1.0/3, 0.25, 1, 0.5, 3},
            {0.5, 1.0/3, 2, 1, 4}, {0.25, 0.2, 1.0/3, 0.25, 1}
        };
        double[][] matriksC5 = {
            {1, 2, 4, 0.5, 5}, {0.5, 1, 3, 1.0/3, 4}, {0.25, 1.0/3, 1, 0.2, 2},
            {2, 3, 5, 1, 6}, {0.2, 0.25, 0.5, 1.0/6, 1}
        };
        double[][] matriksC6 = {
            {1, 0.5, 3, 2, 4}, {2, 1, 4, 3, 5}, {1.0/3, 0.25, 1, 0.5, 3},
            {0.5, 1.0/3, 2, 1, 4}, {0.25, 0.2, 1.0/3, 0.25, 1}
        };
        double[][] matriksC7 = {
            {1, 0.5, 3, 0.5, 4}, {2, 1, 4, 2, 5}, {1.0/3, 0.25, 1, 1.0/3, 3},
            {2, 0.5, 3, 1, 4}, {0.25, 0.2, 1.0/3, 0.25, 1}
        };
        double[][] matriksC8 = {
            {1, 0.5, 4, 2, 5}, {2, 1, 5, 3, 6}, {0.25, 0.2, 1, 0.5, 3},
            {0.5, 1.0/3, 2, 1, 4}, {0.2, 1.0/6, 1.0/3, 0.25, 1}
        };

        double[] bobotC2 = AHPCalculator.hitungBobotPrioritas(matriksC2);
        double[] bobotC3 = AHPCalculator.hitungBobotPrioritas(matriksC3);
        double[] bobotC4 = AHPCalculator.hitungBobotPrioritas(matriksC4);
        double[] bobotC5 = AHPCalculator.hitungBobotPrioritas(matriksC5);
        double[] bobotC6 = AHPCalculator.hitungBobotPrioritas(matriksC6);
        double[] bobotC7 = AHPCalculator.hitungBobotPrioritas(matriksC7);
        double[] bobotC8 = AHPCalculator.hitungBobotPrioritas(matriksC8);

        // bobotAlternatif[kriteria][alternatif]
        double[][] bobotAlternatif = {bobotC1, bobotC2, bobotC3, bobotC4, bobotC5, bobotC6, bobotC7, bobotC8};

        double[] nilaiAkhir = AHPCalculator.hitungNilaiAkhir(hasil.bobotPrioritas, bobotAlternatif);
        System.out.println("Nilai Akhir (expected: A2 tertinggi ~0.40):");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  %s: %.3f\n", namaAuditor[i], nilaiAkhir[i]);
        }

        // Rangking
        int[] rangking = AHPCalculator.getRangking(nilaiAkhir);
        System.out.println("\nPeringkat:");
        for (int rank = 0; rank < 5; rank++) {
            System.out.printf("  %d. %s (%.3f)\n", rank + 1, namaAuditor[rangking[rank]], nilaiAkhir[rangking[rank]]);
        }

        // Assertion sederhana
        System.out.println("\n=== VALIDASI ===");
        boolean pass = true;
        pass &= check("CR < 0.10", hasil.cr < 0.10);
        pass &= check("C4 bobot tertinggi", hasil.bobotPrioritas[3] > hasil.bobotPrioritas[0]);
        pass &= check("A2 (Mia) peringkat 1", rangking[0] == 1);
        pass &= check("A1 (Putra) peringkat 2", rangking[1] == 0);
        pass &= check("A4 (Andre) peringkat 3", rangking[2] == 3);

        System.out.println(pass ? "\nSEMUA TEST PASS!" : "\nADA TEST YANG GAGAL!");
    }

    private static boolean check(String label, boolean condition) {
        System.out.printf("  [%s] %s\n", condition ? "OK" : "FAIL", label);
        return condition;
    }
}
