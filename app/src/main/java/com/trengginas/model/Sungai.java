package com.trengginas.model;

public class Sungai {
    private String id;
    private String idA;
    private String tahun;
    private String idSungai;
    private String nama;
    private String panjang;
    private String lebarPerm;
    private String lebarDasar;
    private String kedalaman;

    // Constructor
    public Sungai(String id, String idA, String tahun, String idSungai, String nama, String panjang, String lebarPerm, String lebarDasar, String kedalaman) {
        this.id = id;
        this.idA = idA;
        this.tahun = tahun;
        this.idSungai = idSungai;
        this.nama = nama;
        this.panjang = panjang;
        this.lebarPerm = lebarPerm;
        this.lebarDasar = lebarDasar;
        this.kedalaman = kedalaman;
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

    public String getIdSungai() {
        return idSungai;
    }

    public String getNama() {
        return nama;
    }

    public String getPanjang() {
        return panjang;
    }

    public String getLebarPerm() {
        return lebarPerm;
    }

    public String getLebarDasar() {
        return lebarDasar;
    }

    public String getKedalaman() {
        return kedalaman;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
}

