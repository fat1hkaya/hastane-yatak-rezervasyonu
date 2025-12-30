package com.hospital.service;

import com.hospital.model.Yatak;
import com.hospital.model.YatakDurumu;
import com.hospital.repository.YatakRepository;
import java.util.List;

public class YatakServisi {
    private final YatakRepository yatakDeposu = new YatakRepository();

    public void topluYatakEkle(String blokNo, int katNo, String odaNo, String odaTuru, int yatakSayisi, String bakimTuru) {

        if (blokNo == null || blokNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Blok adı boş bırakılamaz!");
        }

        if (odaNo == null || odaNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Oda numarası boş bırakılamaz!");
        }

        if (yatakSayisi <= 0) {
            throw new IllegalArgumentException("Yatak sayısı en az 1 olmalıdır!");
        }

        for (int i = 1; i <= yatakSayisi; i++) {
            Yatak yeniYatak = new Yatak();
            yeniYatak.setBlokNo(blokNo);
            yeniYatak.setKatNo(katNo);
            yeniYatak.setOdaNumarasi(odaNo);
            yeniYatak.setYatakNumarasi(String.valueOf(i));
            yeniYatak.setOdaTuru(odaTuru);
            yeniYatak.setYatakSayisi(yatakSayisi);
            yeniYatak.setBakimTuru(bakimTuru);
            yeniYatak.setDurum(YatakDurumu.Boş);

            yatakDeposu.yatakKaydet(yeniYatak);
        }
    }

    public boolean odaMevcutMu(String blokNo, int katNo, String odaNo) {
        return yatakDeposu.odaVarMi(blokNo, katNo, odaNo);
    }

    public List<Yatak> tumYataklariListele() {
        return yatakDeposu.tumYataklariGetir();
    }

    public void yatakSil(int id) {
        yatakDeposu.yatakSil(id);
    }

    public void yatakDurumunuGuncelle(int id, YatakDurumu durum) {
        yatakDeposu.durumGuncelle(id, durum);
    }
}