package AirQualityApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller of Start window
 */
public class StartController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button startBtn;

    @FXML
    void startBtnOnAction(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/airqualityapp.fxml"));
            Parent root = loader.load();

            Stage myScene = (Stage) startBtn.getScene().getWindow();
            myScene.close();

            Controller mainController = loader.getController();
            Stage stage = new Stage();
            Scene s = new Scene(root);
            stage.setScene(s);
            s.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    void initialize() {
        assert startBtn != null : "fx:id=\"startBtn\" was not injected: check your FXML file 'start.fxml'.";

    }
}
