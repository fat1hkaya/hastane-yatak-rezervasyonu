package com.hospital.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VeritabaniBaglantisi {

    private static final String URL = "jdbc:mysql://localhost:3306/hastane_otomasyon_db";
    private static final String KULLANICI = "root";
    private static final String SIFRE = "";

    public static Connection baglantiAl() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, KULLANICI, SIFRE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Veritabanı Sürücüsü bulunamadı: " + e.getMessage());
        }
    }
}