package com.hospital.util;

import com.hospital.config.VeritabaniBaglantisi;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class VeritabaniHazirlayici {

    public static void tablolariOlustur() {
        String yataklarTablosu = """
            CREATE TABLE IF NOT EXISTS yataklar (
                id INT AUTO_INCREMENT PRIMARY KEY,
                blok_no VARCHAR(50),
                kat_no INT,
                oda_no VARCHAR(50) NOT NULL,
                yatak_no VARCHAR(50) NOT NULL,
                oda_turu VARCHAR(50),
                bakim_turu VARCHAR(50),
                durum VARCHAR(20) DEFAULT 'Boş'
            ); """;

        String hastalarTablosu = """
            CREATE TABLE IF NOT EXISTS hastalar (
                id INT AUTO_INCREMENT PRIMARY KEY,
                ad VARCHAR(100) NOT NULL,
                soyad VARCHAR(100) NOT NULL,
                tc_no VARCHAR(11) UNIQUE NOT NULL,
                dogum_tarihi DATE,
                durum VARCHAR(50)
            ); """;

        String rezervasyonlarTablosu = """
            CREATE TABLE IF NOT EXISTS rezervasyonlar (
                id INT AUTO_INCREMENT PRIMARY KEY,
                yatak_id INT NOT NULL,
                hasta_id INT NOT NULL,
                giris_tarihi DATE NOT NULL,
                cikis_tarihi DATE NOT NULL,
                FOREIGN KEY (yatak_id) REFERENCES yataklar(id) ON DELETE CASCADE,
                FOREIGN KEY (hasta_id) REFERENCES hastalar(id) ON DELETE CASCADE
            ); """;

        try (Connection baglanti = VeritabaniBaglantisi.baglantiAl();
             Statement ifade = baglanti.createStatement()) {

            ifade.execute(yataklarTablosu);
            ifade.execute(hastalarTablosu);
            ifade.execute(rezervasyonlarTablosu);

            kolonlariKontrolEtVeEkle(ifade);

            System.out.println("Veritabanı Oluşturuldu.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void kolonlariKontrolEtVeEkle(Statement ifade) {
        String[] migrations = {
                "ALTER TABLE yataklar ADD COLUMN IF NOT EXISTS bakim_turu VARCHAR(50) AFTER oda_turu",
                "ALTER TABLE hastalar ADD COLUMN IF NOT EXISTS durum VARCHAR(50) AFTER dogum_tarihi"
        };

        for (String sql : migrations) {
            try {
                ifade.execute(sql);
            } catch (SQLException e) {
            }
        }
    }
}