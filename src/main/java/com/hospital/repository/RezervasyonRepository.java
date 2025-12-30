package com.hospital.repository;

import com.hospital.config.VeritabaniBaglantisi;
import com.hospital.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RezervasyonRepository {

    public void rezervasyonKaydet(Rezervasyon rezervasyon) {
        String sql = "INSERT INTO rezervasyonlar (yatak_id, hasta_id, giris_tarihi, cikis_tarihi) VALUES (?, ?, ?, ?)";

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {

            statement.setInt(1, rezervasyon.getYatak().getId());
            statement.setInt(2, rezervasyon.getHasta().getId());
            statement.setDate(3, java.sql.Date.valueOf(rezervasyon.getGirisTarihi()));
            statement.setDate(4, java.sql.Date.valueOf(rezervasyon.getCikisTarihi()));

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Rezervasyon kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public List<Rezervasyon> tumRezervasyonlariGetir() {
        List<Rezervasyon> liste = new ArrayList<>();

        String sql = """
            SELECT r.id, r.hasta_id, r.yatak_id, r.giris_tarihi, r.cikis_tarihi,
                   h.ad, h.soyad, h.tc_no, h.dogum_tarihi, h.durum as hasta_durumu,
                   y.blok_no, y.kat_no, y.oda_no, y.yatak_no, y.oda_turu, y.yatak_sayisi, y.bakim_turu, y.durum as yatak_durumu
            FROM rezervasyonlar r
            INNER JOIN hastalar h ON r.hasta_id = h.id
            INNER JOIN yataklar y ON r.yatak_id = y.id
            """;

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             Statement statement = baglanti.createStatement();
             ResultSet sonuc = statement.executeQuery(sql)) {

            while (sonuc.next()) {
                Date hDogumDate = sonuc.getDate("dogum_tarihi");
                Hasta hasta = new Hasta(
                        sonuc.getInt("hasta_id"),
                        sonuc.getString("ad"),
                        sonuc.getString("soyad"),
                        sonuc.getString("tc_no"),
                        (hDogumDate != null) ? hDogumDate.toLocalDate() : null,
                        sonuc.getString("hasta_durumu")
                );

                Yatak yatak = new Yatak(
                        sonuc.getInt("yatak_id"),
                        sonuc.getString("blok_no"),
                        sonuc.getInt("kat_no"),
                        sonuc.getString("oda_no"),
                        sonuc.getString("yatak_no"),
                        sonuc.getString("oda_turu"),
                        sonuc.getInt("yatak_sayisi"),
                        sonuc.getString("bakim_turu"),
                        YatakDurumu.valueOf(sonuc.getString("yatak_durumu"))
                );

                Date gTarihi = sonuc.getDate("giris_tarihi");
                Date cTarihi = sonuc.getDate("cikis_tarihi");

                if (gTarihi != null) yatak.setGirisTarihi(gTarihi.toLocalDate());
                if (cTarihi != null) yatak.setCikisTarihi(cTarihi.toLocalDate());

                Rezervasyon rez = new Rezervasyon(
                        sonuc.getInt("id"),
                        hasta,
                        yatak,
                        (gTarihi != null) ? gTarihi.toLocalDate() : null,
                        (cTarihi != null) ? cTarihi.toLocalDate() : null
                );
                liste.add(rez);
            }
        } catch (SQLException e) {
            System.err.println("Rezervasyonlar listelenirken hata oluştu: " + e.getMessage());
        }
        return liste;
    }

    public boolean hastaninAktifRezervasyonuVarMi(int hastaId) {
        String sql = "SELECT COUNT(*) FROM rezervasyonlar WHERE hasta_id = ?";
        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {

            statement.setInt(1, hastaId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Rezervasyon kontrolü sırasında hata oluştu: " + e.getMessage());
        }
        return false;
    }
}