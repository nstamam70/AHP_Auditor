/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author USER
 */
public class Auditor {
    private int idAuditor;
    private String kodeAuditor;
    private String namaAuditor;
    private String jabatan;
    private String status;
    
    public Auditor() {
    }
    
    public Auditor(int idAuditor, String kodeAuditor, String namaAuditor, String jabatan, String status) {
        this.idAuditor = idAuditor;
        this.kodeAuditor = kodeAuditor;
        this.namaAuditor = namaAuditor;
        this.jabatan = jabatan;
        this.status = status;
    }
    
    public int getIdAuditor() {
        return idAuditor;
    }

    public void setIdAuditor(int idAuditor) {
        this.idAuditor = idAuditor;
    }

    public String getKodeAuditor() {
        return kodeAuditor;
    }

    public void setKodeAuditor(String kodeAuditor) {
        this.kodeAuditor = kodeAuditor;
    }

    public String getNamaAuditor() {
        return namaAuditor;
    }

    public void setNamaAuditor(String namaAuditor) {
        this.namaAuditor = namaAuditor;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
