package com.trengginas.model;

public class Home {
    private String judul;
    private String subjudul;
    private String nmscreen;
    private String icon;

    public Home(String judul, String subjudul, String nmscreen, String icon) {
        this.judul = judul;
        this.subjudul = subjudul;
        this.nmscreen = nmscreen;
        this.icon = icon;
    }

    public String getJudul() {
        return judul;
    }

    public String getSubjudul() {
        return subjudul;
    }

    public String getNmscreen() {
        return nmscreen;
    }

    public String getIcon(){
        return icon;
    }

}
