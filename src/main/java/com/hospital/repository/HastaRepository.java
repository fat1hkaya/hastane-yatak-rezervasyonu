package com.hospital.repository;

import com.hospital.config.VeritabaniBaglantisi;
import com.hospital.model.Hasta;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HastaRepository {

    public void hastaKaydet(Hasta hasta) {
        String sql = "INSERT INTO hastalar (ad, soyad, tc_no, dogum_tarihi, durum) VALUES (?, ?, ?, ?, ?)";

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {

            statement.setString(1, hasta.getAd());
            statement.setString(2, hasta.getSoyad());
            statement.setString(3, hasta.getTcKimlikNo());
            statement.setDate(4, Date.valueOf(hasta.getDogumTarihi()));
            statement.setString(5, hasta.getDurum());

            statement.executeUpdate();
            System.out.println("Hasta başarıyla kaydedildi: " + hasta.getTamAd());

        } catch (SQLException e) {
            System.err.println("Hasta kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public boolean tcNoVarMi(String tcNo) {
        String sql = "SELECT 1 FROM hastalar WHERE tc_no = ?";
        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             PreparedStatement statement = baglanti.prepareStatement(sql)) {
            statement.setString(1, tcNo);
            ResultSet sonuc = statement.executeQuery();
            return sonuc.next();
        } catch (SQLException e) {
            System.err.println("TC numarası kontrol edilirken hata: " + e.getMessage());
            return false;
        }
    }

    public List<Hasta> tumHastalariGetir() {
        List<Hasta> hastaListesi = new ArrayList<>();
        String sql = "SELECT * FROM hastalar";

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             Statement statement = baglanti.createStatement();
             ResultSet sonuc = statement.executeQuery(sql)) {

            while (sonuc.next()) {
                Date sqlDate = sonuc.getDate("dogum_tarihi");
                LocalDate dogumTarihi = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                Hasta hasta = new Hasta(
                        sonuc.getInt("id"),
                        sonuc.getString("ad"),
                        sonuc.getString("soyad"),
                        sonuc.getString("tc_no"),
                        dogumTarihi,
                        sonuc.getString("durum")
                );
                hastaListesi.add(hasta);
            }

        } catch (SQLException e) {
            System.err.println("Hasta listesi çekilirken hata oluştu: " + e.getMessage());
        }
        return hastaListesi;
    }
}