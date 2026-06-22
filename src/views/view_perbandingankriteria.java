/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import dao.KriteriaDAO;
import dao.PerbandinganDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Kriteria;
import utils.AHPCalculator;

/**
 *
 * @author USER
 */
public class view_perbandingankriteria extends javax.swing.JPanel {

    private KriteriaDAO kriteriaDAO;
    private PerbandinganDAO perbandinganDAO;
    private List<Kriteria> kriteriaList;

    /**
     * Creates new form view_dashboard
     */
    public view_perbandingankriteria() {
        initComponents();
        kriteriaDAO = new KriteriaDAO();
        perbandinganDAO = new PerbandinganDAO();
        loadKriteria();
        loadNilaiComboBox();
        addButtonListeners();
        refreshTabel();
    }

    private void loadKriteria() {
        kriteriaList = kriteriaDAO.getAll();
        tkriteria1.removeAllItems();
        tkriteria2.removeAllItems();
        for (Kriteria k : kriteriaList) {
            tkriteria1.addItem(k.getKodeKriteria() + " - " + k.getNamaKriteria());
            tkriteria2.addItem(k.getKodeKriteria() + " - " + k.getNamaKriteria());
        }
        if (tkriteria2.getItemCount() > 1) {
            tkriteria2.setSelectedIndex(1);
        }
    }

    private void loadNilaiComboBox() {
        tnilai.removeAllItems();
        tnilai.addItem("1 - Sama Penting");
        tnilai.addItem("2 - Mendekati Sedikit Lebih Penting");
        tnilai.addItem("3 - Sedikit Lebih Penting");
        tnilai.addItem("4 - Mendekati Lebih Penting");
        tnilai.addItem("5 - Lebih Penting");
        tnilai.addItem("6 - Mendekati Sangat Penting");
        tnilai.addItem("7 - Sangat Penting");
        tnilai.addItem("8 - Mendekati Mutlak Lebih Penting");
        tnilai.addItem("9 - Mutlak Lebih Penting");
        tnilai.addItem("1/2 - Mendekati Sama Penting (kebalikan)");
        tnilai.addItem("1/3 - Sedikit Kurang Penting (kebalikan)");
        tnilai.addItem("1/4 - Mendekati Kurang Penting (kebalikan)");
        tnilai.addItem("1/5 - Kurang Penting (kebalikan)");
        tnilai.addItem("1/6 - Mendekati Sangat Kurang Penting (kebalikan)");
        tnilai.addItem("1/7 - Sangat Kurang Penting (kebalikan)");
        tnilai.addItem("1/8 - Mendekati Mutlak Kurang Penting (kebalikan)");
        tnilai.addItem("1/9 - Mutlak Kurang Penting (kebalikan)");
    }

    private double parseNilai() {
        String selected = (String) tnilai.getSelectedItem();
        if (selected == null) return 1;
        String angka = selected.split(" - ")[0].trim();
        if (angka.startsWith("1/")) {
            int denom = Integer.parseInt(angka.substring(2));
            return 1.0 / denom;
        }
        return Double.parseDouble(angka);
    }

    private void addButtonListeners() {
        btnsimpan.addActionListener(e -> simpanPerbandingan());
        btnubah.addActionListener(e -> simpanPerbandingan()); // update = simpan ulang (ON DUPLICATE KEY UPDATE)
        btnhapus.addActionListener(e -> hapusPerbandingan());
        btnbatal.addActionListener(e -> resetForm());
    }

    private void simpanPerbandingan() {
        int idx1 = tkriteria1.getSelectedIndex();
        int idx2 = tkriteria2.getSelectedIndex();
        if (idx1 == idx2) {
            JOptionPane.showMessageDialog(this, "Kriteria 1 dan Kriteria 2 tidak boleh sama!");
            return;
        }
        int id1 = kriteriaList.get(idx1).getIdKriteria();
        int id2 = kriteriaList.get(idx2).getIdKriteria();
        double nilai = parseNilai();

        boolean ok = perbandinganDAO.simpanPerbandinganKriteria(id1, id2, nilai);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Perbandingan berhasil disimpan!");
            refreshTabel();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perbandingan!");
        }
    }

    private void hapusPerbandingan() {
        int idx1 = tkriteria1.getSelectedIndex();
        int idx2 = tkriteria2.getSelectedIndex();
        if (idx1 == idx2) return;
        int id1 = kriteriaList.get(idx1).getIdKriteria();
        int id2 = kriteriaList.get(idx2).getIdKriteria();

        boolean ok = perbandinganDAO.hapusPerbandinganKriteria(id1, id2);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Perbandingan berhasil dihapus!");
            refreshTabel();
        }
    }

    private void resetForm() {
        if (tkriteria1.getItemCount() > 0) tkriteria1.setSelectedIndex(0);
        if (tkriteria2.getItemCount() > 1) tkriteria2.setSelectedIndex(1);
        if (tnilai.getItemCount() > 0) tnilai.setSelectedIndex(0);
    }

    private void refreshTabel() {
        if (kriteriaList == null || kriteriaList.isEmpty()) return;

        int n = kriteriaList.size();
        double[][] matriks = perbandinganDAO.buildMatriksKriteria(kriteriaList);

        // Tabel matriks perbandingan
        String[] header = new String[n + 1];
        header[0] = "Kriteria";
        for (int i = 0; i < n; i++) {
            header[i + 1] = kriteriaList.get(i).getKodeKriteria();
        }

        Object[][] dataMatriks = new Object[n][n + 1];
        for (int i = 0; i < n; i++) {
            dataMatriks[i][0] = kriteriaList.get(i).getKodeKriteria();
            for (int j = 0; j < n; j++) {
                dataMatriks[i][j + 1] = String.format("%.3f", matriks[i][j]);
            }
        }
        tblmatriksperbandingankriteria.setModel(new DefaultTableModel(dataMatriks, header));

        // Tabel normalisasi + bobot
        double[][] normalized = AHPCalculator.normalisasi(matriks);
        double[] bobot = AHPCalculator.hitungBobotPrioritas(matriks);

        String[] headerNorm = new String[n + 2];
        headerNorm[0] = "Kriteria";
        for (int i = 0; i < n; i++) {
            headerNorm[i + 1] = kriteriaList.get(i).getKodeKriteria();
        }
        headerNorm[n + 1] = "Bobot";

        Object[][] dataNorm = new Object[n][n + 2];
        for (int i = 0; i < n; i++) {
            dataNorm[i][0] = kriteriaList.get(i).getKodeKriteria();
            for (int j = 0; j < n; j++) {
                dataNorm[i][j + 1] = String.format("%.3f", normalized[i][j]);
            }
            dataNorm[i][n + 1] = String.format("%.3f", bobot[i]);
        }
        tblnormalisasikriteria.setModel(new DefaultTableModel(dataNorm, headerNorm));
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tkriteria1 = new javax.swing.JComboBox<>();
        tkriteria2 = new javax.swing.JComboBox<>();
        tnilai = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JSeparator();
        btnsimpan = new javax.swing.JButton();
        btnubah = new javax.swing.JButton();
        btnhapus = new javax.swing.JButton();
        btnbatal = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblmatriksperbandingankriteria = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblnormalisasikriteria = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Perbandingan Kriteria");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Kriteria 1");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel4.setText("Kriteria 2");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel5.setText("Nilai");

        tkriteria1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N

        tkriteria2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N

        tnilai.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N

        btnsimpan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnsimpan.setText("Simpan");

        btnubah.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnubah.setText("Ubah");

        btnhapus.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnhapus.setText("Hapus");

        btnbatal.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        btnbatal.setText("Batal");

        tblmatriksperbandingankriteria.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblmatriksperbandingankriteria);

        tblnormalisasikriteria.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblnormalisasikriteria);

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("Tabel Matriks Perbandingan");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel6.setText("Tabel Normalisasi");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(86, 86, 86)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(btnsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnubah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnhapus, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tkriteria1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tkriteria2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tnilai, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane1)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(457, 457, 457)
                        .addComponent(jLabel1)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tkriteria1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tkriteria2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tnilai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnsimpan)
                    .addComponent(btnubah)
                    .addComponent(btnhapus)
                    .addComponent(btnbatal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnbatal;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btnsimpan;
    private javax.swing.JButton btnubah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable tblmatriksperbandingankriteria;
    private javax.swing.JTable tblnormalisasikriteria;
    private javax.swing.JComboBox<String> tkriteria1;
    private javax.swing.JComboBox<String> tkriteria2;
    private javax.swing.JComboBox<String> tnilai;
    // End of variables declaration//GEN-END:variables
}
