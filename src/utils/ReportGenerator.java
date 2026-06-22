/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import configs.KoneksiDB;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ryumaaa
 */
public class ReportGenerator {

    public static void showReport(String jrxmlPath) {
        showReport(jrxmlPath, null);
    }

    public static void showReport(String jrxmlPath, Map<String, Object> params) {
        try {
            Connection conn = KoneksiDB.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Koneksi database gagal!");
                return;
            }

            InputStream stream = ReportGenerator.class.getResourceAsStream(jrxmlPath);

            if (stream == null) {
                JOptionPane.showMessageDialog(null, "File report tidak ditemukan: " + jrxmlPath);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(stream);

            Map<String, Object> parameters = (params != null) ? params : new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showReportWithDataSource(String jrxmlPath, JRDataSource dataSource) {
        showReportWithDataSource(jrxmlPath, null, dataSource);
    }

    public static void showReportWithDataSource(String jrxmlPath, Map<String, Object> params, JRDataSource dataSource) {
        try {
            InputStream stream = ReportGenerator.class.getResourceAsStream(jrxmlPath);

            if (stream == null) {
                JOptionPane.showMessageDialog(null, "File report tidak ditemukan: " + jrxmlPath);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(stream);

            Map<String, Object> parameters = (params != null) ? params : new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
