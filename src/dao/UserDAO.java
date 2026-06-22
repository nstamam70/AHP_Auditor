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
import models.User;

/**
 *
 * @author ryumaaa
 */
public class UserDAO {

    private Connection conn;

    public UserDAO() {
        this.conn = KoneksiDB.getConnection();
    }

    // kode untuk mengecek username & password
    public boolean register(User user) {

        String sql = "INSERT INTO users (username, password) VALUES (?, MD5(?))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println("Error Register : " + e.getMessage());

        }

        return false;
    }

    public List<User> search(String keyword) {

        List<User> list = new ArrayList<>();

        String sql = "SELECT * FROM users "
                + "WHERE nama_lengkap LIKE ? "
                + "OR username LIKE ? "
                + "ORDER BY id_user ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(mapResultSet(rs));

            }

        } catch (SQLException e) {

            System.err.println("Error Search User : "
                    + e.getMessage());

        }

        return list;
    }

    public boolean updateDenganPassword(User user) {

        String sql
                = "UPDATE `user` SET "
                + "username = ?, "
                + "password = MD5(?), "
                + "WHERE username = ?";

        try (PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println("Error Update : "
                    + e.getMessage());
        }

        return false;
    }

//    public boolean updateTanpaPassword(User user) {
//
//        String sql
//                = "UPDATE `user` SET "
//                + "username = ?, "
//                + "role = ? "
//                + "WHERE nama_lengkap = ?";
//
//        try (PreparedStatement ps
//                = conn.prepareStatement(sql)) {
//
//            ps.setString(1, user.getUsername());
//            ps.setString(2, user.getRole());
//            ps.setString(3, user.getNamaLengkap());
//
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//
//            System.err.println("Error Update : "
//                    + e.getMessage());
//        }
//
//        return false;
//    }

//    public boolean update(User user) {
//
//        String sql
//                = "UPDATE user SET "
//                + "username=?, "
//                + "nama_lengkap=?, "
//                + "role=?, "
//                + "status=? "
//                + "WHERE id_user=?";
//
//        try (PreparedStatement ps
//                = conn.prepareStatement(sql)) {
//
//            ps.setString(1, user.getUsername());
//            ps.setString(2, user.getNamaLengkap());
//            ps.setString(3, user.getRole());
//            ps.setString(4, user.getStatus());
//            ps.setInt(5, user.getIdUser());
//
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//
//            System.err.println("Error update user : "
//                    + e.getMessage());
//
//        }
//
//        return false;
//    }

    public boolean cekUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {

            System.err.println("Error Cek Username : " + e.getMessage());

        }

        return false;
    }

    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ? AND password = MD5(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
        }
        return user;
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUser(rs.getInt("id_user"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        return user;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id_user ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getAll user: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String namaLengkap) {

        String sql = "DELETE FROM `users` WHERE username = ?";

        try (PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setString(1, namaLengkap);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println("Error Delete : "
                    + e.getMessage());
        }

        return false;
    }
}
