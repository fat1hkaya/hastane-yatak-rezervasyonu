package com.hospital.model;

import java.time.LocalDate;

public class Yatak {
    private int id;
    private String blokNo;
    private int katNo;
    private String odaNumarasi;
    private String yatakNumarasi;
    private String odaTuru;
    private int yatakSayisi;
    private String bakimTuru;
    private YatakDurumu durum;
    private LocalDate girisTarihi;
    private LocalDate cikisTarihi;

    private String tcNo;
    private String hastaAdSoyad;

    public Yatak() {
    }

    public Yatak(int id, String blokNo, int katNo, String odaNumarasi, String yatakNumarasi,
                 String odaTuru, int yatakSayisi, String bakimTuru, YatakDurumu durum) {
        this.id = id;
        this.blokNo = blokNo;
        this.katNo = katNo;
        this.odaNumarasi = odaNumarasi;
        this.yatakNumarasi = yatakNumarasi;
        this.odaTuru = odaTuru;
        this.yatakSayisi = yatakSayisi;
        this.bakimTuru = bakimTuru;
        this.durum = durum;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBlokNo() { return blokNo; }
    public void setBlokNo(String blokNo) { this.blokNo = blokNo; }

    public int getKatNo() { return katNo; }
    public void setKatNo(int katNo) { this.katNo = katNo; }

    public String getOdaNumarasi() { return odaNumarasi; }
    public void setOdaNumarasi(String odaNumarasi) { this.odaNumarasi = odaNumarasi; }

    public String getYatakNumarasi() { return yatakNumarasi; }
    public void setYatakNumarasi(String yatakNumarasi) { this.yatakNumarasi = yatakNumarasi; }

    public String getOdaTuru() { return odaTuru; }
    public void setOdaTuru(String odaTuru) { this.odaTuru = odaTuru; }

    public int getYatakSayisi() { return yatakSayisi; }
    public void setYatakSayisi(int yatakSayisi) { this.yatakSayisi = yatakSayisi; }

    public String getBakimTuru() { return bakimTuru; }
    public void setBakimTuru(String bakimTuru) { this.bakimTuru = bakimTuru; }

    public YatakDurumu getDurum() { return durum; }
    public void setDurum(YatakDurumu durum) { this.durum = durum; }

    public LocalDate getGirisTarihi() { return girisTarihi; }
    public void setGirisTarihi(LocalDate girisTarihi) { this.girisTarihi = girisTarihi; }

    public LocalDate getCikisTarihi() { return cikisTarihi; }
    public void setCikisTarihi(LocalDate cikisTarihi) { this.cikisTarihi = cikisTarihi; }

    public String getTcNo() { return tcNo; }
    public void setTcNo(String tcNo) { this.tcNo = tcNo; }

    public String getHastaAdSoyad() { return hastaAdSoyad; }
    public void setHastaAdSoyad(String hastaAdSoyad) { this.hastaAdSoyad = hastaAdSoyad; }

    @Override
    public String toString() {
        return "Blok: " + blokNo + " Kat: " + katNo + " Oda: " + odaNumarasi;
    }
}