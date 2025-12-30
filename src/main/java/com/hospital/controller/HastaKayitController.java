package com.hospital.controller;

import com.hospital.model.Hasta;
import com.hospital.service.HastaServisi;
import com.hospital.util.SahneYoneticisi;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class HastaKayitController {
    @FXML private TextField txtAd, txtSoyad, txtTcNo;
    @FXML private DatePicker dpDogumTarihi;
    @FXML private ComboBox<String> comboBakim;

    @FXML private TableView<Hasta> hastaTablosu;
    @FXML private TableColumn<Hasta, String> sutunAd, sutunSoyad, sutunTc, sutunDurum;
    @FXML private TableColumn<Hasta, LocalDate> sutunDogumTarihi;

    private final HastaServisi hastaServisi = new HastaServisi();

    @FXML
    public void initialize() {
        comboBakim.setItems(FXCollections.observableArrayList(
                "Yoğun Bakım",
                "Orta Düzey Bakım",
                "Standart Bakım"
        ));

        sutunAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        sutunSoyad.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        sutunTc.setCellValueFactory(new PropertyValueFactory<>("tcKimlikNo"));
        sutunDogumTarihi.setCellValueFactory(new PropertyValueFactory<>("dogumTarihi"));
        sutunDurum.setCellValueFactory(new PropertyValueFactory<>("durum"));

        tabloyuGuncelle();
    }

    @FXML
    private void hastaKaydet() {
        try {
            if (comboBakim.getValue() == null) {
                throw new Exception("Lütfen bakım gereksinimini seçiniz!");
            }

            hastaServisi.hastaKaydet(
                    txtAd.getText(),
                    txtSoyad.getText(),
                    txtTcNo.getText(),
                    dpDogumTarihi.getValue(),
                    comboBakim.getValue()
            );

            temizle();
            tabloyuGuncelle();
            alertGoster(Alert.AlertType.INFORMATION, "Başarılı", "Hasta başarıyla kaydedildi.");
        } catch (Exception e) {
            alertGoster(Alert.AlertType.ERROR, "Hata", e.getMessage());
        }
    }

    private void tabloyuGuncelle() {
        if (hastaServisi.tumHastalariListele() != null) {
            hastaTablosu.setItems(FXCollections.observableArrayList(hastaServisi.tumHastalariListele()));
        }
    }

    private void temizle() {
        txtAd.clear();
        txtSoyad.clear();
        txtTcNo.clear();
        dpDogumTarihi.setValue(null);
        comboBakim.setValue(null);
    }

    @FXML
    private void geriDon(ActionEvent event) {
        SahneYoneticisi.sahneDegistir(event, "/fxml/ana_panel.fxml", "Ana Panel");
    }

    private void alertGoster(Alert.AlertType tip, String baslik, String icerik) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
}