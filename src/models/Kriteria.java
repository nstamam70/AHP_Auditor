/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author USER
 */
public class Kriteria {

    private int idKriteria;
    private String kodeKriteria;
    private String namaKriteria;

    // Constructor Kosong
    public Kriteria() {
    }

    // Constructor dengan Parameter
    public Kriteria(int idKriteria, String kodeKriteria, String namaKriteria) {
        this.idKriteria = idKriteria;
        this.kodeKriteria = kodeKriteria;
        this.namaKriteria = namaKriteria;
    }

    // Getter dan Setter
    public int getIdKriteria() {
        return idKriteria;
    }

    public void setIdKriteria(int idKriteria) {
        this.idKriteria = idKriteria;
    }

    public String getKodeKriteria() {
        return kodeKriteria;
    }

    public void setKodeKriteria(String kodeKriteria) {
        this.kodeKriteria = kodeKriteria;
    }

    public String getNamaKriteria() {
        return namaKriteria;
    }

    public void setNamaKriteria(String namaKriteria) {
        this.namaKriteria = namaKriteria;
    }
}
