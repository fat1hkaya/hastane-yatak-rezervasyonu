package com.hospital.controller;

import com.hospital.model.Yatak;
import com.hospital.model.YatakDurumu;
import com.hospital.service.YatakServisi;
import com.hospital.util.SahneYoneticisi;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class AnaPanelController {
    @FXML private TableView<Yatak> yatakTablosu;
    @FXML private TableColumn<Yatak, String> sutunBlok;
    @FXML private TableColumn<Yatak, Integer> sutunKat;
    @FXML private TableColumn<Yatak, String> sutunOda, sutunYatak, sutunOdaTuru, sutunBakim;
    @FXML private TableColumn<Yatak, String> sutunHastaTC;
    @FXML private TableColumn<Yatak, String> sutunHastaAd;
    @FXML private TableColumn<Yatak, YatakDurumu> sutunDurum;
    @FXML private TableColumn<Yatak, LocalDate> sutunGirisTarihi, sutunCikisTarihi;

    @FXML private TextField txtBlokNo, txtKatNo, txtOdaNo, txtOzelYatakSayisi;
    @FXML private ComboBox<String> comboOdaTuru, comboBakimTuru;

    private final YatakServisi yatakServisi = new YatakServisi();

    @FXML
    public void initialize() {
        sutunBlok.setCellValueFactory(new PropertyValueFactory<>("blokNo"));
        sutunKat.setCellValueFactory(new PropertyValueFactory<>("katNo"));
        sutunOda.setCellValueFactory(new PropertyValueFactory<>("odaNumarasi"));
        sutunOdaTuru.setCellValueFactory(new PropertyValueFactory<>("odaTuru"));
        sutunYatak.setCellValueFactory(new PropertyValueFactory<>("yatakNumarasi"));
        sutunBakim.setCellValueFactory(new PropertyValueFactory<>("bakimTuru"));
        sutunDurum.setCellValueFactory(new PropertyValueFactory<>("durum"));
        sutunHastaAd.setCellValueFactory(new PropertyValueFactory<>("hastaAdSoyad"));
        sutunHastaTC.setCellValueFactory(new PropertyValueFactory<>("tcNo"));
        sutunGirisTarihi.setCellValueFactory(new PropertyValueFactory<>("girisTarihi"));
        sutunCikisTarihi.setCellValueFactory(new PropertyValueFactory<>("cikisTarihi"));

        yatakTablosu.setRowFactory(tv -> new TableRow<Yatak>() {
            @Override
            protected void updateItem(Yatak item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getDurum() == YatakDurumu.Rezerve) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (item.getDurum() == YatakDurumu.Bakımda) {
                        setStyle("-fx-background-color: #fff4cc;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        comboOdaTuru.setItems(FXCollections.observableArrayList("Tek Yataklı", "İki Yataklı", "Üç Yataklı", "Çok Yataklı"));
        comboBakimTuru.setItems(FXCollections.observableArrayList("Yoğun Bakım", "Orta Düzey Bakım", "Standart Bakım"));

        txtOzelYatakSayisi.setVisible(false);
        comboOdaTuru.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            txtOzelYatakSayisi.setVisible("Çok Yataklı".equals(newVal));
        });

        verileriYukle();
    }

    private void verileriYukle() {
        yatakTablosu.setItems(FXCollections.observableArrayList(yatakServisi.tumYataklariListele()));
        yatakTablosu.refresh();
    }

    @FXML
    private void yatakBosalt() {
        Yatak secili = yatakTablosu.getSelectionModel().getSelectedItem();
        if (secili == null) {
            uyariGoster("Seçim Yapılmadı", "Lütfen işlem yapılacak yatağı seçin.");
            return;
        }
        yatakServisi.yatakDurumunuGuncelle(secili.getId(), YatakDurumu.Boş);
        verileriYukle();
    }

    @FXML
    private void yatakRezerveEt(ActionEvent event) {
        Yatak secili = yatakTablosu.getSelectionModel().getSelectedItem();
        if (secili == null) {
            uyariGoster("Seçim Yapılmadı", "Lütfen rezerve edilecek yatağı seçin.");
            return;
        }
        if (secili.getDurum() != YatakDurumu.Boş) {
            uyariGoster("Mantık Hatası", "Sadece 'Boş' durumdaki yataklar rezerve edilebilir!");
            return;
        }

        FXMLLoader loader = SahneYoneticisi.sahneDegistir(event, "/fxml/rezervasyon.fxml", "Yeni Rezervasyon");

        if (loader != null) {
            RezervasyonController controller = loader.getController();
            controller.yatakSeciminiOnTanimla(secili);
        }
    }

    @FXML
    private void yatakBakimaAl() {
        Yatak secili = yatakTablosu.getSelectionModel().getSelectedItem();
        if (secili == null) {
            uyariGoster("Seçim Yapılmadı", "Lütfen bakıma alınacak yatağı seçin.");
            return;
        }
        if (secili.getDurum() != YatakDurumu.Boş) {
            uyariGoster("Güvenlik Engeli", "Yatağı bakıma almadan önce 'Boşalt' işlemini yapmanız gerekmektedir.");
            return;
        }
        yatakServisi.yatakDurumunuGuncelle(secili.getId(), YatakDurumu.Bakımda);
        verileriYukle();
    }

    @FXML
    private void yatakEkle() {
        try {
            if (txtBlokNo.getText().isEmpty() || txtKatNo.getText().isEmpty() || txtOdaNo.getText().isEmpty() || comboOdaTuru.getValue() == null || comboBakimTuru.getValue() == null) {
                uyariGoster("Eksik Bilgi", "Lütfen tüm zorunlu alanları doldurun!");
                return;
            }

            String blok = txtBlokNo.getText();
            int kat = Integer.parseInt(txtKatNo.getText());
            String oda = txtOdaNo.getText();
            String tur = comboOdaTuru.getValue();
            String bakim = comboBakimTuru.getValue();
            int kapasite;

            if (yatakServisi.odaMevcutMu(blok, kat, oda)) {
                uyariGoster("Kayıt Hatası", blok + " blokta " + oda + " numaralı oda zaten mevcut.");
                return;
            }

            if ("Çok Yataklı".equals(tur)) {
                kapasite = Integer.parseInt(txtOzelYatakSayisi.getText());
            } else {
                kapasite = switch (tur) {
                    case "Tek Yataklı" -> 1;
                    case "İki Yataklı" -> 2;
                    case "Üç Yataklı" -> 3;
                    default -> 0;
                };
            }

            yatakServisi.topluYatakEkle(blok, kat, oda, tur, kapasite, bakim);
            verileriYukle();
            temizleAlanlar();
        } catch (Exception e) {
            uyariGoster("Hata", "Giriş verilerini kontrol edin.");
        }
    }

    @FXML
    private void yatakSil() {
        Yatak secili = yatakTablosu.getSelectionModel().getSelectedItem();
        if (secili == null) {
            uyariGoster("Seçim Yapılmadı", "Lütfen silmek istediğiniz yatağı seçin.");
            return;
        }

        if (secili.getDurum() == YatakDurumu.Rezerve) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kritik Engel");
            alert.setHeaderText("Silme İşlemi Reddedildi!");
            alert.setContentText("Bu yatak şu anda bir hastaya REZERVE edilmiş durumdadır.\nDolayısı ile silinemez.");
            alert.showAndWait();
            return;
        }

        Alert onay = new Alert(Alert.AlertType.CONFIRMATION);
        onay.setTitle("Silme Onayı");
        onay.setHeaderText("Dikkat!");
        onay.setContentText("Bu yatağı sistemden tamamen silmek istediğinize emin misiniz?");

        if (onay.showAndWait().get() == ButtonType.OK) {
            yatakServisi.yatakSil(secili.getId());
            verileriYukle();
        }
    }

    private void temizleAlanlar() {
        txtBlokNo.clear();
        txtKatNo.clear();
        txtOdaNo.clear();
        txtOzelYatakSayisi.clear();
        comboOdaTuru.getSelectionModel().clearSelection();
        comboBakimTuru.getSelectionModel().clearSelection();
    }

    @FXML private void hastalariGoster(ActionEvent e) { SahneYoneticisi.sahneDegistir(e, "/fxml/hasta_kayit.fxml", "Hasta Kayıt"); }
    @FXML private void rezervasyonGoster(ActionEvent e) { SahneYoneticisi.sahneDegistir(e, "/fxml/rezervasyon.fxml", "Rezervasyon"); }

    private void uyariGoster(String baslik, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }
}