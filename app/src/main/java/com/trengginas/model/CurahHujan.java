package com.trengginas.model;

public class CurahHujan {
    private String id;
    private String idA;
    private String tahun;
    private String kdBl;
    private String bulan;
    private String curah;
    private String hari;

    // Constructor
    public CurahHujan(String id, String idA, String tahun, String kdBl, String bulan, String curah, String hari) {
        this.id = id;
        this.idA = idA;
        this.tahun = tahun;
        this.kdBl = kdBl;
        this.bulan = bulan;
        this.curah = curah;
        this.hari = hari;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getIdA() {
        return idA;
    }

    public String getTahun() {
        return tahun;
    }

    public String getKdBl() {
        return kdBl;
    }

    public String getBulan() {
        return bulan;
    }

    public String getCurah() {
        return curah;
    }

    public String getHari() {
        return hari;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
}

