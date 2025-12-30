package com.hospital.model;

import java.time.LocalDate;

public class Rezervasyon {
    private int id;
    private Hasta hasta;
    private Yatak yatak;
    private LocalDate girisTarihi;
    private LocalDate cikisTarihi;

    public Rezervasyon(int id, Hasta hasta, Yatak yatak, LocalDate girisTarihi, LocalDate cikisTarihi) {
        this.id = id;
        this.hasta = hasta;
        this.yatak = yatak;
        this.girisTarihi = girisTarihi;
        this.cikisTarihi = cikisTarihi;
    }

    public int getId() { return id; }
    public Hasta getHasta() { return hasta; }
    public Yatak getYatak() { return yatak; }
    public LocalDate getGirisTarihi() { return girisTarihi; }
    public LocalDate getCikisTarihi() { return cikisTarihi; }
}