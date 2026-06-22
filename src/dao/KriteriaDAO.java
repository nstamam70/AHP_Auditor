/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import configs.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Auditor;
import models.Kriteria;

/**
 *
 * @author USER
 */
public class KriteriaDAO {

    private Connection conn;

    public KriteriaDAO() {
        this.conn = KoneksiDB.getConnection();
    }

    public String generateKode() {
        String sql = "SELECT kode_kriteria FROM kriteria ORDER BY id_kriteria DESC LIMIT 1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastKode = rs.getString("kode_kriteria");
                int number = Integer.parseInt(lastKode.replace("KRT-", "")) + 1;
                return String.format("KRT-%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("Error generateKode: " + e.getMessage());
        }
        return "KRT-001";
    }

    public List<Kriteria> search(String keyword) {
        List<Kriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM kriteria WHERE nama_kriteria LIKE ? OR kode_kriteria LIKE ? ORDER BY kode_kriteria ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs)); // Memanggil mapResultSet milik KriteriaDAO
                }
            }
        } catch (SQLException e) {
            System.err.println("Error search kriteria: " + e.getMessage());
        }
        return list;
    }

    // 1. MAP RESULT SET (Pemetaan dari DB ke Model Kriteria)
    private Kriteria mapResultSet(ResultSet rs) throws SQLException {
        Kriteria k = new Kriteria();
        k.setIdKriteria(rs.getInt("id_kriteria"));
        k.setKodeKriteria(rs.getString("kode_kriteria"));
        k.setNamaKriteria(rs.getString("nama_kriteria"));
        return k;
    }

    // 2. GET BY KODE
    public Kriteria getByKode(String kode) {
        String sql = "SELECT * FROM kriteria WHERE kode_kriteria = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Get By Kode Kriteria: " + e.getMessage());
        }
        return null;
    }

    // 3. GET ALL DATA
    public List<Kriteria> getAll() {
        List<Kriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM kriteria";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Kriteria kriteria = new Kriteria();
                // Memetakan dari nama kolom DB ke setter model camelCase
                kriteria.setIdKriteria(rs.getInt("id_kriteria"));
                kriteria.setKodeKriteria(rs.getString("kode_kriteria"));
                kriteria.setNamaKriteria(rs.getString("nama_kriteria"));

                list.add(kriteria);
            }
        } catch (SQLException e) {
            System.err.println("Error Get All Kriteria: " + e.getMessage());
        }
        return list;
    }

    // 4. INSERT (Tambah Data)
    public boolean insert(Kriteria kriteria) {
        String sql = "INSERT INTO kriteria (kode_kriteria, nama_kriteria) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kriteria.getKodeKriteria());
            ps.setString(2, kriteria.getNamaKriteria());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Kriteria: " + e.getMessage());
        }
        return false;
    }

    // 5. UPDATE (Ubah Data)
    public boolean update(Kriteria kriteria) {
        String sql = "UPDATE kriteria SET kode_kriteria = ?, nama_kriteria = ? WHERE id_kriteria = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kriteria.getKodeKriteria());
            ps.setString(2, kriteria.getNamaKriteria());
            ps.setInt(3, kriteria.getIdKriteria());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Kriteria: " + e.getMessage());
        }
        return false;
    }

    // 6. DELETE (Hapus Data)
    public boolean delete(int idKriteria) {
        String sql = "DELETE FROM kriteria WHERE id_kriteria = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Kriteria: " + e.getMessage());
        }
        return false;
    }
}
