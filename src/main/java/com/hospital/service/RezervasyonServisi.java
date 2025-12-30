package com.hospital.service;

import com.hospital.model.*;
import com.hospital.repository.RezervasyonRepository;
import com.hospital.repository.YatakRepository;
import java.util.List;

public class RezervasyonServisi {
    private final RezervasyonRepository rezDeposu = new RezervasyonRepository();
    private final YatakRepository yatakDeposu = new YatakRepository();

    public void rezervasyonOlustur(Rezervasyon rez) {
        if (rezDeposu.hastaninAktifRezervasyonuVarMi(rez.getHasta().getId())) {
            throw new RuntimeException("Bu hasta zaten başka bir yatakta kayıtlı! Aynı anda iki yatak rezerve edilemez.");
        }

        if (rez.getYatak().getDurum() != YatakDurumu.Boş) {
            throw new IllegalStateException("Seçilen yatak şu an müsait değil (Durum: " + rez.getYatak().getDurum() + ")");
        }

        if (rez.getCikisTarihi().isBefore(rez.getGirisTarihi())) {
            throw new IllegalArgumentException("Çıkış tarihi, giriş tarihinden önce olamaz!!!");
        }

        rezDeposu.rezervasyonKaydet(rez);
        yatakDeposu.durumGuncelle(rez.getYatak().getId(), YatakDurumu.Rezerve);
        System.out.println("Rezervasyon işlemi ve yatak durum güncellemesi tamamlandı.");
    }

    public List<Rezervasyon> tumRezervasyonlariListele() {
        return rezDeposu.tumRezervasyonlariGetir();
    }

    public void hastayiTaburcuEt(Rezervasyon rez) {
        yatakDeposu.durumGuncelle(rez.getYatak().getId(), YatakDurumu.Bakımda);
        System.out.println("Hasta taburcu edildi, yatak BAKIMDA moduna alındı.");
    }
}