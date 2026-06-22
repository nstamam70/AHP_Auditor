    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package configs;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author USER
 */
public class KoneksiDB {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ahp_auditor";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static java.sql.Connection connection;

    public static java.sql.Connection getConnection() {
        if (connection == null) {
            try {
                // Load driver JDBC
                Class.forName("com.mysql.jdbc.Driver");
                
                // Buat koneksi
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Koneksi ke database berhasil!");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver tidak ditemukan! ");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Koneksi ke database gagal! ");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
