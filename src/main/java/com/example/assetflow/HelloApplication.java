package com.example.assetflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        stage.setTitle("Asset Flow");
        stage.setScene(scene);
        stage.show();
    }
}
