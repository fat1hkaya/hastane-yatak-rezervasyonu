package com.hospital.repository;

import com.hospital.config.VeritabaniBaglantisi;
import com.hospital.model.Yatak;
import com.hospital.model.YatakDurumu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YatakRepository {

    public void yatakKaydet(Yatak yatak) {
        String sql = "INSERT INTO yataklar (blok_no, kat_no, oda_no, yatak_no, oda_turu, yatak_sayisi, bakim_turu, durum) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {
            statement.setString(1, yatak.getBlokNo());
            statement.setInt(2, yatak.getKatNo());
            statement.setString(3, yatak.getOdaNumarasi());
            statement.setString(4, yatak.getYatakNumarasi());
            statement.setString(5, yatak.getOdaTuru());
            statement.setInt(6, yatak.getYatakSayisi());
            statement.setString(7, yatak.getBakimTuru());
            statement.setString(8, yatak.getDurum().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Yatak kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public void yatakSil(int id) {
        String sql = "DELETE FROM yataklar WHERE id = ?";
        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement ifade = baglanti.prepareStatement(sql)) {
            ifade.setInt(1, id);
            ifade.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Yatak silinirken hata oluştu: " + e.getMessage());
        }
    }

    public List<Yatak> tumYataklariGetir() {
        List<Yatak> yatakListesi = new ArrayList<>();
        String sql = "SELECT y.*, h.tc_no, CONCAT(h.ad, ' ', h.soyad) as hasta_ad_soyad, " +
                "r.giris_tarihi as res_giris, r.cikis_tarihi as res_cikis " +
                "FROM yataklar y " +
                "LEFT JOIN rezervasyonlar r ON y.id = r.yatak_id " +
                "LEFT JOIN hastalar h ON r.hasta_id = h.id";

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             Statement statement = baglanti.createStatement();
             ResultSet sonuc = statement.executeQuery(sql)) {

            while (sonuc.next()) {
                Yatak yatak = new Yatak();
                yatak.setId(sonuc.getInt("id"));
                yatak.setBlokNo(sonuc.getString("blok_no"));
                yatak.setKatNo(sonuc.getInt("kat_no"));
                yatak.setOdaNumarasi(sonuc.getString("oda_no"));
                yatak.setYatakNumarasi(sonuc.getString("yatak_no"));
                yatak.setOdaTuru(sonuc.getString("oda_turu"));
                yatak.setYatakSayisi(sonuc.getInt("yatak_sayisi"));
                yatak.setBakimTuru(sonuc.getString("bakim_turu"));
                yatak.setDurum(YatakDurumu.valueOf(sonuc.getString("durum")));

                yatak.setTcNo(sonuc.getString("tc_no"));
                yatak.setHastaAdSoyad(sonuc.getString("hasta_ad_soyad"));

                Date giris = sonuc.getDate("res_giris");
                Date cikis = sonuc.getDate("res_cikis");

                if (giris != null) yatak.setGirisTarihi(giris.toLocalDate());
                if (cikis != null) yatak.setCikisTarihi(cikis.toLocalDate());

                yatakListesi.add(yatak);
            }
        } catch (SQLException e) {
            System.err.println("Yatak listesi getirilirken hata oluştu: " + e.getMessage());
        }
        return yatakListesi;
    }

    public void durumGuncelle(int yatakId, YatakDurumu yeniDurum) {
        String updateYatakSql = "UPDATE yataklar SET durum = ? WHERE id = ?";
        String deleteRezervasyonSql = "DELETE FROM rezervasyonlar WHERE yatak_id = ?";

        Connection baglanti = null;
        try {
            baglanti = VeritabaniBaglantisi.baglantiAl();
            baglanti.setAutoCommit(false);

            try (PreparedStatement psYatak = baglanti.prepareStatement(updateYatakSql)) {
                psYatak.setString(1, yeniDurum.name());
                psYatak.setInt(2, yatakId);
                psYatak.executeUpdate();
            }

            if (yeniDurum == YatakDurumu.Boş) {
                try (PreparedStatement psRez = baglanti.prepareStatement(deleteRezervasyonSql)) {
                    psRez.setInt(1, yatakId);
                    psRez.executeUpdate();
                }
            }

            baglanti.commit();
        } catch (SQLException e) {
            if (baglanti != null) {
                try { baglanti.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Durum güncellenirken hata oluştu: " + e.getMessage());
        } finally {
            if (baglanti != null) {
                try { baglanti.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean odaVarMi(String blokNo, int katNo, String odaNo) {
        String sql = "SELECT 1 FROM yataklar WHERE blok_no = ? AND kat_no = ? AND oda_no = ? LIMIT 1";
        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {
            statement.setString(1, blokNo);
            statement.setInt(2, katNo);
            statement.setString(3, odaNo);
            try (ResultSet sonuc = statement.executeQuery()) {
                return sonuc.next();
            }
        } catch (SQLException e) {
            System.err.println("Oda mevcudiyeti kontrol edilirken hata oluştu: " + e.getMessage());
            return false;
        }
    }
}