package com.hospital.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SahneYoneticisi {

    public static FXMLLoader sahneDegistir(ActionEvent event, String fxmlYolu, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader(SahneYoneticisi.class.getResource(fxmlYolu));
            Parent kok = loader.load();

            Stage sahne = (Stage) ((Node) event.getSource()).getScene().getWindow();
            sahne.setTitle(baslik);

            if (sahne.getScene() == null) {
                sahne.setScene(new Scene(kok));
            } else {
                sahne.getScene().setRoot(kok);
            }
            if (fxmlYolu.contains("ana_panel.fxml")) {
                sahne.setMaximized(true);
            } else if(fxmlYolu.contains("hasta_kayit.fxml")){
                sahne.setMaximized(false);
                sahne.setWidth(650);
                sahne.setHeight(900);
            }else {
                sahne.setMaximized(false);
                sahne.setWidth(900);
                sahne.setHeight(600);
            }
            return loader;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}