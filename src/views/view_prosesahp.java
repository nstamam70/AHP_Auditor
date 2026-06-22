/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import dao.AuditorDAO;
import dao.KriteriaDAO;
import dao.PerbandinganDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Auditor;
import models.Kriteria;
import utils.AHPCalculator;

/**
 *
 * @author USER
 */
public class view_prosesahp extends javax.swing.JPanel {

    private KriteriaDAO kriteriaDAO;
    private AuditorDAO auditorDAO;
    private PerbandinganDAO perbandinganDAO;
    private List<Kriteria> kriteriaList;
    private List<Auditor> auditorList;

    /**
     * Creates new form view_dashboard
     */
    public view_prosesahp() {
        initComponents();
        kriteriaDAO = new KriteriaDAO();
        auditorDAO = new AuditorDAO();
        perbandinganDAO = new PerbandinganDAO();
        kriteriaList = kriteriaDAO.getAll();
        auditorList = auditorDAO.getAll();
    }

    private void hitungHasilAkhir() {
        if (kriteriaList.isEmpty() || auditorList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data kriteria atau auditor belum tersedia!");
            return;
        }

        int nKriteria = kriteriaList.size();
        int nAlternatif = auditorList.size();

        // 1. Hitung bobot kriteria
        double[][] matriksKriteria = perbandinganDAO.buildMatriksKriteria(kriteriaList);
        AHPCalculator.HasilAHP hasilKriteria = AHPCalculator.proses(matriksKriteria);

        // Tampilkan tabel hasil perbandingan kriteria (normalisasi + bobot)
        tampilTabelKriteria(hasilKriteria);

        // Tampilkan CI, CR, Lambda kriteria
        lblCi.setText(String.format("CI: %.4f", hasilKriteria.ci));
        lblCr.setText(String.format("CR: %.4f", hasilKriteria.cr));
        lblLambda.setText(String.format("λ: %.4f", hasilKriteria.lambdaMax));

        if (!hasilKriteria.konsisten) {
            JOptionPane.showMessageDialog(this,
                "Perbandingan kriteria TIDAK konsisten (CR = " +
                String.format("%.4f", hasilKriteria.cr) + " >= 0.10).\n" +
                "Silakan perbaiki nilai perbandingan kriteria.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        // 2. Hitung bobot alternatif per kriteria
        double[][] bobotAlternatif = new double[nKriteria][nAlternatif];
        double ciAltTotal = 0, crAltTotal = 0, lambdaAltTotal = 0;

        for (int k = 0; k < nKriteria; k++) {
            int idKriteria = kriteriaList.get(k).getIdKriteria();
            double[][] matriksAlt = perbandinganDAO.buildMatriksAlternatif(idKriteria, auditorList);
            AHPCalculator.HasilAHP hasilAlt = AHPCalculator.proses(matriksAlt);
            bobotAlternatif[k] = hasilAlt.bobotPrioritas;
            ciAltTotal += hasilAlt.ci;
            crAltTotal += hasilAlt.cr;
            lambdaAltTotal += hasilAlt.lambdaMax;
        }

        // Tampilkan tabel hasil perbandingan alternatif (bobot per kriteria)
        tampilTabelAlternatif(bobotAlternatif);

        // Tampilkan rata-rata CI, CR, Lambda alternatif
        lblCi1.setText(String.format("CI: %.4f", ciAltTotal / nKriteria));
        lblCr1.setText(String.format("CR: %.4f", crAltTotal / nKriteria));
        lblLambda1.setText(String.format("λ: %.4f", lambdaAltTotal / nKriteria));

        // 3. Hitung nilai akhir (prioritas global)
        double[] nilaiAkhir = AHPCalculator.hitungNilaiAkhir(hasilKriteria.bobotPrioritas, bobotAlternatif);
        int[] rangking = AHPCalculator.getRangking(nilaiAkhir);

        // Tampilkan tabel hasil akhir
        tampilTabelHasilAkhir(nilaiAkhir, rangking);
    }

    private void tampilTabelKriteria(AHPCalculator.HasilAHP hasil) {
        int n = kriteriaList.size();
        String[] header = new String[n + 2];
        header[0] = "Kriteria";
        for (int i = 0; i < n; i++) {
            header[i + 1] = kriteriaList.get(i).getKodeKriteria();
        }
        header[n + 1] = "Bobot";

        Object[][] data = new Object[n][n + 2];
        for (int i = 0; i < n; i++) {
            data[i][0] = kriteriaList.get(i).getKodeKriteria() + " - " + kriteriaList.get(i).getNamaKriteria();
            for (int j = 0; j < n; j++) {
                data[i][j + 1] = String.format("%.3f", hasil.matriksNormalisasi[i][j]);
            }
            data[i][n + 1] = String.format("%.3f", hasil.bobotPrioritas[i]);
        }
        jTable1.setModel(new DefaultTableModel(data, header));
    }

    private void tampilTabelAlternatif(double[][] bobotAlternatif) {
        int nKriteria = kriteriaList.size();
        int nAlt = auditorList.size();

        String[] header = new String[nKriteria + 1];
        header[0] = "Alternatif";
        for (int k = 0; k < nKriteria; k++) {
            header[k + 1] = kriteriaList.get(k).getKodeKriteria();
        }

        Object[][] data = new Object[nAlt][nKriteria + 1];
        for (int i = 0; i < nAlt; i++) {
            data[i][0] = auditorList.get(i).getKodeAuditor() + " - " + auditorList.get(i).getNamaAuditor();
            for (int k = 0; k < nKriteria; k++) {
                data[i][k + 1] = String.format("%.3f", bobotAlternatif[k][i]);
            }
        }
        jTable2.setModel(new DefaultTableModel(data, header));
    }

    private void tampilTabelHasilAkhir(double[] nilaiAkhir, int[] rangking) {
        String[] header = {"Peringkat", "Kode", "Nama Auditor", "Nilai Akhir"};
        Object[][] data = new Object[rangking.length][4];
        for (int rank = 0; rank < rangking.length; rank++) {
            int idx = rangking[rank];
            data[rank][0] = rank + 1;
            data[rank][1] = auditorList.get(idx).getKodeAuditor();
            data[rank][2] = auditorList.get(idx).getNamaAuditor();
            data[rank][3] = String.format("%.3f", nilaiAkhir[idx]);
        }
        jTable3.setModel(new DefaultTableModel(data, header));
    }

    private void navigateTo(javax.swing.JPanel view) {
        java.awt.Container parent = this.getParent();
        if (parent != null) {
            parent.removeAll();
            parent.add(view);
            parent.repaint();
            parent.revalidate();
        }
    }

    private void resetHasil() {
        jTable1.setModel(new DefaultTableModel());
        jTable2.setModel(new DefaultTableModel());
        jTable3.setModel(new DefaultTableModel());
        lblCi.setText("CI");
        lblCr.setText("CR");
        lblLambda.setText("Lambda");
        lblCi1.setText("CI");
        lblCr1.setText("CR");
        lblLambda1.setText("Lambda");
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
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblCi = new javax.swing.JTextField();
        lblCr = new javax.swing.JTextField();
        lblLambda = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        lblCi1 = new javax.swing.JTextField();
        lblCr1 = new javax.swing.JTextField();
        lblLambda1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        scrollbar1 = new java.awt.Scrollbar();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Proses AHP");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("Hasil Perbandingan Kriteria");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        lblCi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblCi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblCi.setText("CI");
        lblCi.setEditable(false);

        lblCr.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblCr.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblCr.setText("CR");
        lblCr.setEditable(false);

        lblLambda.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblLambda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblLambda.setText("Lambda");
        lblLambda.setEditable(false);

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Hasil Perbandingan Alternatif");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        lblCi1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblCi1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblCi1.setText("CI");
        lblCi1.setEditable(false);

        lblCr1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblCr1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblCr1.setText("CR");
        lblCr1.setEditable(false);

        lblLambda1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        lblLambda1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblLambda1.setText("Lambda");
        lblLambda1.setEditable(false);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel4.setText("Hasil Akhir");

        scrollbar1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                scrollbar1AdjustmentValueChanged(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton3.setText("Reset Hasil");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton2.setText("Hitung Hasil Akhir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton1.setText("Perbandingan Kriteria");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navigateTo(new view_perbandingankriteria());
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton4.setText("Perbandingan Alternatif");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navigateTo(new view_perbandinganalternatif());
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(451, 451, 451)
                                .addComponent(jLabel1))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(14, 14, 14)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblCi, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(lblCr, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(lblLambda, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1082, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(lblCi1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(lblCr1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(lblLambda1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollbar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLambda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCi1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLambda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4))
                        .addGap(0, 75, Short.MAX_VALUE))
                    .addComponent(scrollbar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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

    private void scrollbar1AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_scrollbar1AdjustmentValueChanged
        int nilaiScroll = scrollbar1.getValue();
        jPanel1.setLocation(jPanel1.getX(), -nilaiScroll);
    }//GEN-LAST:event_scrollbar1AdjustmentValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        resetHasil();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        hitungHasilAkhir();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField lblCi;
    private javax.swing.JTextField lblCi1;
    private javax.swing.JTextField lblCr;
    private javax.swing.JTextField lblCr1;
    private javax.swing.JTextField lblLambda;
    private javax.swing.JTextField lblLambda1;
    private java.awt.Scrollbar scrollbar1;
    // End of variables declaration//GEN-END:variables
}
