/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import dao.AuditorDAO;
import dao.KriteriaDAO;
import dao.PerbandinganDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Auditor;
import models.Kriteria;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import utils.AHPCalculator;
import utils.ReportGenerator;

/**
 *
 * @author USER
 */
public class view_laporan extends javax.swing.JPanel {

    /**
     * Creates new form view_dashboard
     */
    public view_laporan() {
        initComponents();
        btnlaporanhasilahp.setText("Perbandingan Alternatif");
        btnlaporandatakriteria.addActionListener(e -> ReportGenerator.showReport("/reports/report_kriteria.jrxml"));
        btnlaporanperangkingan.addActionListener(e -> cetakLaporanPerangkingan());
        btnlaporanhasilahp.addActionListener(e -> cetakLaporanPerbandinganAlternatif());
        btnlaporandataauditor.addActionListener(e -> ReportGenerator.showReport("/reports/report_auditor.jrxml"));
    }

    private void cetakLaporanPerangkingan() {
        KriteriaDAO kriteriaDAO = new KriteriaDAO();
        AuditorDAO auditorDAO = new AuditorDAO();
        PerbandinganDAO perbandinganDAO = new PerbandinganDAO();

        List<Kriteria> kriteriaList = kriteriaDAO.getAll();
        List<Auditor> auditorList = auditorDAO.getAll();

        if (kriteriaList.isEmpty() || auditorList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Data kriteria atau auditor kosong!");
            return;
        }

        int nKriteria = kriteriaList.size();
        int nAlternatif = auditorList.size();

        double[][] matriksKriteria = perbandinganDAO.buildMatriksKriteria(kriteriaList);
        double[] bobotKriteria = AHPCalculator.hitungBobotPrioritas(matriksKriteria);

        double[][] bobotAlternatif = new double[nKriteria][nAlternatif];
        for (int k = 0; k < nKriteria; k++) {
            int idKriteria = kriteriaList.get(k).getIdKriteria();
            double[][] matriksAlt = perbandinganDAO.buildMatriksAlternatif(idKriteria, auditorList);
            bobotAlternatif[k] = AHPCalculator.hitungBobotPrioritas(matriksAlt);
        }

        double[] nilaiAkhir = AHPCalculator.hitungNilaiAkhir(bobotKriteria, bobotAlternatif);
        int[] rangking = AHPCalculator.getRangking(nilaiAkhir);

        List<Map<String, ?>> dataList = new ArrayList<>();
        for (int rank = 0; rank < rangking.length; rank++) {
            int idx = rangking[rank];
            Map<String, Object> row = new HashMap<>();
            row.put("ranking", rank + 1);
            row.put("kode_auditor", auditorList.get(idx).getKodeAuditor());
            row.put("nama_auditor", auditorList.get(idx).getNamaAuditor());
            row.put("nilai_akhir", nilaiAkhir[idx]);
            row.put("persentase", String.format("%.1f%%", nilaiAkhir[idx] * 100));
            dataList.add(row);
        }

        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dataList);
        ReportGenerator.showReportWithDataSource("/reports/report_perangkingan.jrxml", dataSource);
    }

    private void cetakLaporanPerbandinganAlternatif() {
        KriteriaDAO kriteriaDAO = new KriteriaDAO();
        AuditorDAO auditorDAO = new AuditorDAO();
        PerbandinganDAO perbandinganDAO = new PerbandinganDAO();

        List<Kriteria> kriteriaList = kriteriaDAO.getAll();
        List<Auditor> auditorList = auditorDAO.getAll();

        if (kriteriaList.isEmpty() || auditorList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Data kriteria atau auditor kosong!");
            return;
        }

        int nKriteria = kriteriaList.size();
        int nAlternatif = auditorList.size();

        // Hitung bobot prioritas alternatif per kriteria
        double[][] bobotAlternatif = new double[nKriteria][nAlternatif];
        for (int k = 0; k < nKriteria; k++) {
            double[][] matriks = perbandinganDAO.buildMatriksAlternatif(kriteriaList.get(k).getIdKriteria(), auditorList);
            bobotAlternatif[k] = AHPCalculator.hitungBobotPrioritas(matriks);
        }

        // Header kolom = nama kriteria
        Map<String, Object> params = new HashMap<>();
        for (int k = 0; k < nKriteria && k < 5; k++) {
            params.put("col" + (k + 1) + "_header", kriteriaList.get(k).getNamaKriteria());
        }

        // Baris = alternatif, kolom = bobot per kriteria
        List<Map<String, ?>> dataList = new ArrayList<>();
        for (int i = 0; i < nAlternatif; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("alternatif", auditorList.get(i).getNamaAuditor());
            for (int k = 0; k < nKriteria && k < 5; k++) {
                row.put("col" + (k + 1), String.format("%.4f", bobotAlternatif[k][i]));
            }
            dataList.add(row);
        }

        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dataList);
        ReportGenerator.showReportWithDataSource("/reports/report_perbandinganAlternatif.jrxml", params, dataSource);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnlaporandataauditor = new javax.swing.JButton();
        btnlaporandatakriteria = new javax.swing.JButton();
        btnlaporanperangkingan = new javax.swing.JButton();
        btnlaporanhasilahp = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Data Laporan");

        btnlaporandataauditor.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnlaporandataauditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons8-pdf-50.png"))); // NOI18N
        btnlaporandataauditor.setText("Laporan Data Auditor");
        btnlaporandataauditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlaporandataauditorActionPerformed(evt);
            }
        });

        btnlaporandatakriteria.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnlaporandatakriteria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons8-pdf-50.png"))); // NOI18N
        btnlaporandatakriteria.setText("Laporan Data Kriteria");

        btnlaporanperangkingan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnlaporanperangkingan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons8-pdf-50.png"))); // NOI18N
        btnlaporanperangkingan.setText("Perangkingan");

        btnlaporanhasilahp.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnlaporanhasilahp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons8-pdf-50.png"))); // NOI18N
        btnlaporanhasilahp.setText("Hasil Proses AHP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(502, 502, 502))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnlaporanhasilahp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnlaporanperangkingan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnlaporandatakriteria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnlaporandataauditor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 1149, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(139, 139, 139)
                .addComponent(btnlaporandataauditor, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnlaporandatakriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnlaporanperangkingan, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnlaporanhasilahp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnlaporandataauditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlaporandataauditorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnlaporandataauditorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnlaporandataauditor;
    private javax.swing.JButton btnlaporandatakriteria;
    private javax.swing.JButton btnlaporanhasilahp;
    private javax.swing.JButton btnlaporanperangkingan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
