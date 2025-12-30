package com.hospital.service;

import com.hospital.model.Hasta;
import com.hospital.repository.HastaRepository;
import java.time.LocalDate;
import java.util.List;

public class HastaServisi {
    private final HastaRepository hastaDeposu = new HastaRepository();

    public void hastaKaydet(String ad, String soyad, String tcNo, LocalDate dogumTarihi, String durum) {

        if (ad.isEmpty() || soyad.isEmpty() || tcNo.length() != 11 || dogumTarihi == null) {
            throw new IllegalArgumentException("Lütfen bilgileri eksiksiz doldurun ve 11 haneli geçerli bir TC girin!");
        }

        if (hastaDeposu.tcNoVarMi(tcNo)) {
            throw new RuntimeException("Bu TC numarasına sahip bir hasta zaten sistemde kayıtlı!");
        }

        Hasta yeniHasta = new Hasta(0, ad, soyad, tcNo, dogumTarihi, durum);
        hastaDeposu.hastaKaydet(yeniHasta);
    }

    public List<Hasta> tumHastalariListele() {
        return hastaDeposu.tumHastalariGetir();
    }
}