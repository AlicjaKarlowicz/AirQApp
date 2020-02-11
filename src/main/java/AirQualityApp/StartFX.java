package AirQualityApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * JavaFX App
 * Start window
 */
public class StartFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("AirQ - your air quality app");
        Scene s = new Scene(root,500,300);
        s.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setScene(s);
        primaryStage.show();
    }
}
