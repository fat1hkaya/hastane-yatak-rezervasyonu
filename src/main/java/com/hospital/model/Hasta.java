package com.hospital.model;

import java.time.LocalDate;

public class Hasta {
    private int id;
    private String ad;
    private String soyad;
    private String tcKimlikNo;
    private LocalDate dogumTarihi;
    private String durum;

    public Hasta(int id, String ad, String soyad, String tcKimlikNo, LocalDate dogumTarihi, String durum) {
        this.id = id;
        this.ad = ad;
        this.soyad = soyad;
        this.tcKimlikNo = tcKimlikNo;
        this.dogumTarihi = dogumTarihi;
        this.durum = durum;
    }

    public int getId() { return id; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getTcKimlikNo() { return tcKimlikNo; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public String getDurum() { return durum; }

    public String getTamAd() {
        return ad + " " + soyad;
    }

    @Override
    public String toString() {
        return getTamAd() + " TC: " + tcKimlikNo;
    }
}