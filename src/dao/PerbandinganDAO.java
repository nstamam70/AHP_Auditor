package dao;

import configs.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import models.Kriteria;
import models.Auditor;

/**
 * DAO untuk perbandingan berpasangan AHP
 * Sesuai tabel: perbandingan_kriteria (id_perbandingan, id_kriteria_1, id_kriteria_2, nilai)
 *               perbandingan_alternatif (id_perbandingan, id_kriteria, id_auditor_1, id_auditor_2, nilai)
 */
public class PerbandinganDAO {

    private Connection conn;

    public PerbandinganDAO() {
        this.conn = KoneksiDB.getConnection();
    }

    // ===== PERBANDINGAN KRITERIA =====

    public boolean simpanPerbandinganKriteria(int idKriteria1, int idKriteria2, double nilai) {
        // Cek apakah sudah ada, jika ada update, jika belum insert
        String cekSql = "SELECT id_perbandingan FROM perbandingan_kriteria WHERE id_kriteria_1 = ? AND id_kriteria_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(cekSql)) {
            ps.setInt(1, idKriteria1);
            ps.setInt(2, idKriteria2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String sql = "UPDATE perbandingan_kriteria SET nilai = ? WHERE id_perbandingan = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                        ps2.setDouble(1, nilai);
                        ps2.setInt(2, rs.getInt("id_perbandingan"));
                        return ps2.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cek perbandingan kriteria: " + e.getMessage());
        }

        // Insert baru
        String sql = "INSERT INTO perbandingan_kriteria (id_kriteria_1, id_kriteria_2, nilai) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria1);
            ps.setInt(2, idKriteria2);
            ps.setDouble(3, nilai);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error simpan perbandingan kriteria: " + e.getMessage());
        }
        return false;
    }

    public double getPerbandinganKriteria(int idKriteria1, int idKriteria2) {
        if (idKriteria1 == idKriteria2) return 1.0;

        String sql = "SELECT nilai FROM perbandingan_kriteria WHERE id_kriteria_1 = ? AND id_kriteria_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria1);
            ps.setInt(2, idKriteria2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("nilai");
            }
        } catch (SQLException e) {
            System.err.println("Error get perbandingan kriteria: " + e.getMessage());
        }
        return 0;
    }

    public double[][] buildMatriksKriteria(List<Kriteria> kriteriaList) {
        int n = kriteriaList.size();
        double[][] matriks = new double[n][n];

        for (int i = 0; i < n; i++) {
            matriks[i][i] = 1.0;
            for (int j = i + 1; j < n; j++) {
                int id1 = kriteriaList.get(i).getIdKriteria();
                int id2 = kriteriaList.get(j).getIdKriteria();
                double nilai = getPerbandinganKriteria(id1, id2);
                if (nilai > 0) {
                    matriks[i][j] = nilai;
                    matriks[j][i] = 1.0 / nilai;
                } else {
                    double nilaiBalik = getPerbandinganKriteria(id2, id1);
                    if (nilaiBalik > 0) {
                        matriks[j][i] = nilaiBalik;
                        matriks[i][j] = 1.0 / nilaiBalik;
                    } else {
                        matriks[i][j] = 1.0;
                        matriks[j][i] = 1.0;
                    }
                }
            }
        }
        return matriks;
    }

    public boolean hapusPerbandinganKriteria(int idKriteria1, int idKriteria2) {
        String sql = "DELETE FROM perbandingan_kriteria WHERE id_kriteria_1 = ? AND id_kriteria_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria1);
            ps.setInt(2, idKriteria2);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error hapus perbandingan kriteria: " + e.getMessage());
        }
        return false;
    }

    public boolean hapusSemuaPerbandinganKriteria() {
        String sql = "DELETE FROM perbandingan_kriteria";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error hapus semua perbandingan kriteria: " + e.getMessage());
        }
        return false;
    }

    // ===== PERBANDINGAN ALTERNATIF =====

    public boolean simpanPerbandinganAlternatif(int idKriteria, int idAuditor1, int idAuditor2, double nilai) {
        String cekSql = "SELECT id_perbandingan FROM perbandingan_alternatif WHERE id_kriteria = ? AND id_auditor_1 = ? AND id_auditor_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(cekSql)) {
            ps.setInt(1, idKriteria);
            ps.setInt(2, idAuditor1);
            ps.setInt(3, idAuditor2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String sql = "UPDATE perbandingan_alternatif SET nilai = ? WHERE id_perbandingan = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                        ps2.setDouble(1, nilai);
                        ps2.setInt(2, rs.getInt("id_perbandingan"));
                        return ps2.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cek perbandingan alternatif: " + e.getMessage());
        }

        String sql = "INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria);
            ps.setInt(2, idAuditor1);
            ps.setInt(3, idAuditor2);
            ps.setDouble(4, nilai);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error simpan perbandingan alternatif: " + e.getMessage());
        }
        return false;
    }

    public double getPerbandinganAlternatif(int idKriteria, int idAuditor1, int idAuditor2) {
        if (idAuditor1 == idAuditor2) return 1.0;

        String sql = "SELECT nilai FROM perbandingan_alternatif WHERE id_kriteria = ? AND id_auditor_1 = ? AND id_auditor_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria);
            ps.setInt(2, idAuditor1);
            ps.setInt(3, idAuditor2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("nilai");
            }
        } catch (SQLException e) {
            System.err.println("Error get perbandingan alternatif: " + e.getMessage());
        }
        return 0;
    }

    public double[][] buildMatriksAlternatif(int idKriteria, List<Auditor> auditorList) {
        int n = auditorList.size();
        double[][] matriks = new double[n][n];

        for (int i = 0; i < n; i++) {
            matriks[i][i] = 1.0;
            for (int j = i + 1; j < n; j++) {
                int id1 = auditorList.get(i).getIdAuditor();
                int id2 = auditorList.get(j).getIdAuditor();
                double nilai = getPerbandinganAlternatif(idKriteria, id1, id2);
                if (nilai > 0) {
                    matriks[i][j] = nilai;
                    matriks[j][i] = 1.0 / nilai;
                } else {
                    double nilaiBalik = getPerbandinganAlternatif(idKriteria, id2, id1);
                    if (nilaiBalik > 0) {
                        matriks[j][i] = nilaiBalik;
                        matriks[i][j] = 1.0 / nilaiBalik;
                    } else {
                        matriks[i][j] = 1.0;
                        matriks[j][i] = 1.0;
                    }
                }
            }
        }
        return matriks;
    }

    public boolean hapusPerbandinganAlternatif(int idKriteria, int idAuditor1, int idAuditor2) {
        String sql = "DELETE FROM perbandingan_alternatif WHERE id_kriteria = ? AND id_auditor_1 = ? AND id_auditor_2 = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria);
            ps.setInt(2, idAuditor1);
            ps.setInt(3, idAuditor2);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error hapus perbandingan alternatif: " + e.getMessage());
        }
        return false;
    }

    public boolean hapusSemuaPerbandinganAlternatif() {
        String sql = "DELETE FROM perbandingan_alternatif";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error hapus semua perbandingan alternatif: " + e.getMessage());
        }
        return false;
    }
}
