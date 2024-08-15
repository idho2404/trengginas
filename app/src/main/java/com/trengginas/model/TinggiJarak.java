package com.trengginas.model;

public class TinggiJarak {
    private String id;
    private String idA;
    private String tahun;
    private String idKec;
    private String kec;
    private String tinggi;
    private String jarak;

    // Constructor
    public TinggiJarak(String id, String idA, String tahun, String idKec, String kec, String tinggi, String jarak) {
        this.id = id;
        this.idA = idA;
        this.tahun = tahun;
        this.idKec = idKec;
        this.kec = kec;
        this.tinggi = tinggi;
        this.jarak = jarak;
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

    public String getIdKec() {
        return idKec;
    }

    public String getKec() {
        return kec;
    }

    public String getTinggi() {
        return tinggi;
    }

    public String getJarak() {
        return jarak;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
}

