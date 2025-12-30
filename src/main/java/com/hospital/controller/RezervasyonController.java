package com.hospital.controller;

import com.hospital.model.*;
import com.hospital.service.*;
import com.hospital.util.SahneYoneticisi;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RezervasyonController {
    @FXML private TextField txtHastaTC;
    @FXML private Label lblHastaAd, lblHastaBakim, lblFiltreBakim, lblUyari;
    @FXML private VBox vboxHastaBilgi;
    @FXML private ComboBox<Yatak> comboYatak;
    @FXML private ComboBox<String> comboOdaFiltre;
    @FXML private DatePicker dpGiris, dpCikis;

    private final HastaServisi hastaServisi = new HastaServisi();
    private final YatakServisi yatakServisi = new YatakServisi();
    private final RezervasyonServisi rezServisi = new RezervasyonServisi();

    private Hasta bulunanHasta = null;

    @FXML
    public void initialize() {
        comboOdaFiltre.setItems(FXCollections.observableArrayList("Tümü", "Tek Yataklı", "İki Yataklı", "Üç Yataklı", "Çok Yataklı"));
        comboOdaFiltre.setValue("Tümü");

        refreshYatakListesi();
    }

    @FXML
    private void hastaSorgula() {
        String tc = txtHastaTC.getText();
        if (tc == null || tc.isEmpty()) {
            alertGoster("Uyarı", "Lütfen bir TC numarası giriniz.");
            return;
        }

        bulunanHasta = hastaServisi.tumHastalariListele().stream()
                .filter(h -> h.getTcKimlikNo().equals(tc))
                .findFirst()
                .orElse(null);

        if (bulunanHasta != null) {
            lblHastaAd.setText("Ad Soyad: " + bulunanHasta.getTamAd());
            lblHastaBakim.setText("Gereken Bakım: " + bulunanHasta.getDurum());
            lblFiltreBakim.setText("Bakım Filtresi: " + bulunanHasta.getDurum());
            vboxHastaBilgi.setVisible(true);
            lblUyari.setText("Hasta doğrulandı. Uygun yataklar listeleniyor.");
            lblUyari.setStyle("-fx-text-fill: #27ae60;");

            refreshYatakListesi();
        } else {
            vboxHastaBilgi.setVisible(false);
            bulunanHasta = null;
            lblFiltreBakim.setText("Bakım Filtresi: -");
            alertGoster("Bilgi", "Bu TC numarasına kayıtlı hasta bulunamadı.");
            refreshYatakListesi();
        }
    }

    @FXML
    private void filtreUygula() {
        refreshYatakListesi();
    }

    private void refreshYatakListesi() {
        List<Yatak> tumBosYataklar = yatakServisi.tumYataklariListele().stream()
                .filter(y -> y.getDurum() == YatakDurumu.Boş)
                .collect(Collectors.toList());

        List<Yatak> filtrelenmisListe = tumBosYataklar.stream()
                .filter(y -> {
                    boolean bakimUygun = (bulunanHasta == null) || y.getBakimTuru().equals(bulunanHasta.getDurum());

                    String seciliOdaTuru = comboOdaFiltre.getValue();
                    boolean odaUygun = seciliOdaTuru.equals("Tümü") || y.getOdaTuru().equals(seciliOdaTuru);

                    return bakimUygun && odaUygun;
                })
                .collect(Collectors.toList());

        comboYatak.setItems(FXCollections.observableArrayList(filtrelenmisListe));

        if (filtrelenmisListe.isEmpty() && bulunanHasta != null) {
            lblUyari.setText("DİKKAT: Hastanın durumuna uygun boş yatak bulunamadı!");
            lblUyari.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    public void yatakSeciminiOnTanimla(Yatak seciliYatak) {
        if (seciliYatak != null) {
            if (!comboYatak.getItems().contains(seciliYatak)) {
                comboYatak.getItems().add(seciliYatak);
            }
            comboYatak.getSelectionModel().select(seciliYatak);
        }
    }

    @FXML
    private void rezervasyonuKaydet() {
        try {
            Yatak seciliYatak = comboYatak.getValue();
            LocalDate giris = dpGiris.getValue();
            LocalDate cikis = dpCikis.getValue();

            if (bulunanHasta == null || seciliYatak == null || giris == null || cikis == null) {
                alertGoster("Hata", "Lütfen hasta sorgulama yapın ve tüm alanları doldurun!");
                return;
            }

            if (!seciliYatak.getBakimTuru().equals(bulunanHasta.getDurum())) {
                alertGoster("Uygunsuzluk Hatası", "Seçilen yatak hastanın durumuna uygun değildir!");
                return;
            }

            Rezervasyon yeniRez = new Rezervasyon(0, bulunanHasta, seciliYatak, giris, cikis);
            rezServisi.rezervasyonOlustur(yeniRez);
            alertGoster("Başarılı", "Rezervasyon başarıyla oluşturuldu.");
            temizle();
        } catch (Exception e) {
            alertGoster("İşlem Hatası", e.getMessage());
        }
    }

    private void temizle() {
        txtHastaTC.clear();
        vboxHastaBilgi.setVisible(false);
        bulunanHasta = null;
        comboYatak.setValue(null);
        comboOdaFiltre.setValue("Tümü");
        dpGiris.setValue(null);
        dpCikis.setValue(null);
        refreshYatakListesi();
    }

    @FXML
    private void geriDon(ActionEvent event) {
        SahneYoneticisi.sahneDegistir(event, "/fxml/ana_panel.fxml", "Ana Panel");
    }

    private void alertGoster(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
}