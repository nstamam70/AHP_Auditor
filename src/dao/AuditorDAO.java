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

/**
 *
 * @author USER
 */
public class AuditorDAO {

    private Connection conn;

    public AuditorDAO() {
        this.conn = KoneksiDB.getConnection();
    }

    // 1. GET ALL DATA (Tampil Semua Data)
    public List<Auditor> getAll() {
        List<Auditor> list = new ArrayList<>();
        String sql = "SELECT * FROM auditor";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Auditor auditor = new Auditor();
                // Memetakan dari nama kolom DB ke setter model camelCase
                auditor.setIdAuditor(rs.getInt("id_auditor"));
                auditor.setKodeAuditor(rs.getString("kode_auditor"));
                auditor.setNamaAuditor(rs.getString("nama_auditor"));
                auditor.setJabatan(rs.getString("jabatan"));
                auditor.setStatus(rs.getString("status"));

                list.add(auditor);
            }
        } catch (SQLException e) {
            System.err.println("Error Get All Auditor: " + e.getMessage());
        }
        return list;
    }

    // 2. INSERT (Tambah Data)
    public boolean insert(Auditor auditor) {
        String sql = "INSERT INTO auditor (kode_auditor, nama_auditor, jabatan, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, auditor.getKodeAuditor());
            ps.setString(2, auditor.getNamaAuditor());
            ps.setString(3, auditor.getJabatan());
            ps.setString(4, auditor.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Auditor: " + e.getMessage());
        }
        return false;
    }

    // 3. UPDATE (Ubah Data)
    public boolean update(Auditor auditor) {
        String sql = "UPDATE auditor SET kode_auditor = ?, nama_auditor = ?, jabatan = ?, status = ? WHERE id_auditor = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, auditor.getKodeAuditor());
            ps.setString(2, auditor.getNamaAuditor());
            ps.setString(3, auditor.getJabatan());
            ps.setString(4, auditor.getStatus());
            ps.setInt(5, auditor.getIdAuditor()); // WHERE clause berdasarkan ID

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Auditor: " + e.getMessage());
        }
        return false;
    }

    // 4. DELETE (Hapus Data)
    public boolean delete(int idAuditor) {
        String sql = "DELETE FROM auditor WHERE id_auditor = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAuditor);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Auditor: " + e.getMessage());
        }
        return false;
    }

    public String generateKode() {
        String sql = "SELECT kode_auditor FROM auditor ORDER BY id_auditor DESC LIMIT 1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastKode = rs.getString("kode_auditor");
                int number = Integer.parseInt(lastKode.replace("ADT-", "")) + 1;
                return String.format("ADT-%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("Error generateKode: " + e.getMessage());
        }
        return "ADT-001";
    }

    public Auditor getByKode(String kode) {
        String sql = "SELECT * FROM auditor WHERE kode_auditor = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Mengisi parameter tanda tanya (?) dengan variable kode
            ps.setString(1, kode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Menggunakan fungsi mapResultSet yang sudah kita buat sebelumnya
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Get By Kode Auditor: " + e.getMessage());
        }

        return null; // Mengembalikan null jika data tidak ditemukan
    }

    public List<Auditor> search(String keyword) {
        List<Auditor> list = new ArrayList<>();
        String sql = "SELECT * FROM auditor WHERE nama_auditor LIKE ? OR kode_auditor LIKE ? ORDER BY kode_auditor ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error search teknisi: " + e.getMessage());
        }
        return list;
    }

    private Auditor mapResultSet(ResultSet rs) throws SQLException {
        Auditor a = new Auditor();

        // Memetakan dari kolom database ke setter model Auditor
        a.setIdAuditor(rs.getInt("id_auditor"));
        a.setKodeAuditor(rs.getString("kode_auditor"));
        a.setNamaAuditor(rs.getString("nama_auditor"));
        a.setJabatan(rs.getString("jabatan"));
        a.setStatus(rs.getString("status"));

        return a;
    }
}
