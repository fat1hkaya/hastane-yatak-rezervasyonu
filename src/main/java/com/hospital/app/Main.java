package com.hospital.app;

import com.hospital.util.VeritabaniHazirlayici;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage anaSahne) {
        try {
            VeritabaniHazirlayici.tablolariOlustur();

            FXMLLoader yukleyici = new FXMLLoader(getClass().getResource("/fxml/ana_panel.fxml"));
            Parent kokDizin = yukleyici.load();

            anaSahne.setTitle("Hastane Yatak Rezervasyon Otomasyonu");
            anaSahne.setScene(new Scene(kokDizin));
            anaSahne.setMaximized(true);
            anaSahne.show();

        } catch (Exception e) {
            System.err.println("Uygulama başlatma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}