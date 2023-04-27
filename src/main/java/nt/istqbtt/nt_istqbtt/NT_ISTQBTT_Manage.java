package nt.istqbtt.nt_istqbtt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NT_ISTQBTT_Manage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NT_ISTQBTT_Manage.class.getResource("istqbtt_managepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 850);
        stage.setTitle("Manage Page");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}