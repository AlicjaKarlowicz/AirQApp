package AirQualityApp;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.net.StandardSocketOptions;
import java.net.URL;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.CalendarDataProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

/**
 * Controller class for AirQ app
 */
public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField cityTxTField;

    @FXML
    private CheckBox bcCheck;

    @FXML
    private CheckBox coCheck;

    @FXML
    private CheckBox pm10Check;

    @FXML
    private CheckBox pm25Check;

    @FXML
    private CheckBox o3Check;

    @FXML
    private CheckBox no2Check;

    @FXML
    private CheckBox so2Check;

    @FXML
    private Button addBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button saveFileBtn;

    @FXML
    private Button openFileBtn;

    @FXML
    private VBox vBoxResults;

    @FXML
    private Label cityLabel;


    @FXML
    private TextFlow txtFlow;

    @FXML
    private Label statsLabel;

    @FXML
    private ComboBox<String> paramsStatsComboBox;

    @FXML
    private Label nrMesLabel;

    @FXML
    private Label minVLabel;

    @FXML
    private Label maxVLabel;

    @FXML
    private Label stDevLabel;

    @FXML
    private Label aqiLabel;

    @FXML
    private Tab tab2;

    @FXML
    private AnchorPane anchorTab;

    @FXML
    private Button showStatsBtn;

    @FXML
    private Rectangle aqiIndicator;

    @FXML
    private BarChart<Number,String> chart;

    /**
     * To specify the format in which the parameters values will be displayed
     */
    private DecimalFormat df = new DecimalFormat("0.00");

    /**
     * List of current Stats object - that means of every parameter the user recieves
     */
    private  List<Stats> statsList = new ArrayList<>();


    /**
     * Window for opening the session using FileChooser
     * @param event
     */
    @FXML
    void openFileBtnOnAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(openFileBtn.getScene().getWindow());

        if (file != null) {
            openFileMethod(file);
        }
    }

    /**
     * Read the file of previously saved session
     * Use UTF8 encoding/decoding to read file
     * Display the session: average param, get stats, drawchart, display city name
     * @param file
     */
    void openFileMethod(File file) {

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF8")); //UTF8 encoding bc of the polish chars
            String line = br.readLine();

            Gson gson = new GsonBuilder() //read JSON objects
                    .setPrettyPrinting()
                    .create();

            statsList.clear(); //clear the list of current session

            while (line != null) { //read file

                Stats stats = gson.fromJson(line,Stats.class); //cast to Stats class
                statsList.add(stats); //add to statsList
                line = br.readLine();

            }

            clearLabels(cityLabel,maxVLabel,minVLabel,nrMesLabel,stDevLabel,aqiLabel);
            txtFlow.getChildren().clear();
            clearCheckBoxes(bcCheck,coCheck,pm10Check,pm25Check,so2Check,no2Check,o3Check);
            cityTxTField.clear();

            br.close();

            handleComboBox();


            //perform tasks on statsList:display average and populate combo box, draw chart
            for(Stats stat: statsList) {

                cityLabel.setText(stat.getMeasurements().get(0).getCity());

                Text n = new Text(stat.getName().toUpperCase() + "\n");
                Text t = new Text((df.format(stat.getAverage())) + " " + stat.getUnit() + " " + stat.getDate() + "\n\n");
                n.getStyleClass().add("n");
                txtFlow.getChildren().addAll(n,t);

            }


            drawChart();

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Window for saving the current session using FileChooser
     * @param event
     */

    @FXML
    void saveFileBtnOnAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        //save in txt extension

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //show save file dialog
        File file = fileChooser.showSaveDialog(saveFileBtn.getScene().getWindow());

        if (file != null) {
            saveFileMethod(file);
        }

    }

    /**
     * Save current Stats in statsList to file in JSON format
     * @param file
     */
    void saveFileMethod(File file) {

        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fileWriter);

            for(Stats s: statsList){

                bw.write(s.toString()); //JSON format in toSTring(0 methos
                bw.newLine();
            }


            bw.close();
            fileWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Cleat all the stuff in session
     * @param event
     */
    @FXML
    void clearBtnOnAction(ActionEvent event) {

        statsList.clear();
        chart.getData().clear();
        txtFlow.getChildren().clear();
        clearLabels(cityLabel,maxVLabel,minVLabel,nrMesLabel,stDevLabel,aqiLabel);
        clearCheckBoxes(bcCheck,coCheck,pm10Check,pm25Check,so2Check,no2Check,o3Check);
        addBtn.setDisable(false);

    }

    /**
     * Submit entered data such as city and checked parameters
     * Send them further to handleParams(String param) method
     * Eliminate null objects from Stats list: the ones that the param wasn't available for entered city
     * Add all the not null objects to statsList
     * If stats list is  empty that means that the city is invalid: show alert dialog
     * Handle combo box and draw chart
     * @param event
     */
    @FXML
    void addBtnOnAction(ActionEvent event) {


        String enteredCity = cityTxTField.getText();
        List<String> checkBoxes = handleCheckBoxes(no2Check,so2Check,pm10Check,pm25Check,coCheck,bcCheck,o3Check);

        if (!enteredCity.equals("") && !checkBoxes.isEmpty()){

            enteredCity = enteredCity.substring(0,1).toUpperCase() + enteredCity.substring(1).toLowerCase();


            cityLabel.setText(enteredCity);
            paramsStatsComboBox.setDisable(false);
            showStatsBtn.setDisable(false);
            saveFileBtn.setDisable(false);

            List<Stats> prepareStats = new ArrayList<>(); //prepare list of stats

            //some of the objects will be null if the param is not available

                for(String s: checkBoxes) {
                    Stats st = handleParams(s); //get Stats
                    prepareStats.add(st);
                }


                for(Stats st: prepareStats){ //eliminate null objects
                    if(st != null)
                        statsList.add(st);
                }

                if(statsList.isEmpty()){ //if its empty the city is not valid for api
                    addBtn.setDisable(false); //enable user to submit data once again bc there is no data in session
                    saveFileBtn.setDisable(true); //disable the possibility of save: there is no data in session
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid city " + enteredCity + ".");
                    alert.show();
                }

                handleComboBox();

                if(!txtFlow.equals(""))  //if text flow contains params (the results)
                addBtn.setDisable(true); //disable the possibility to add another set of params unless the user first clears session

                drawChart();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Enter city name and select desired parameters.");
            alert.show();
        }

    }

    /**
     * Show statistics for chosen parameter
     * @param event
     */
    @FXML
    void showStatsBtnOnAction(ActionEvent event) {

        if(!paramsStatsComboBox.getValue().equals("")) {

            for (Stats s : statsList)
                handleStats(s);

        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Choose parameter to display stats.");
        }

    }


    /**
     * Draw chart of norm values and searched values
     * Draws only parameters that the user checked in check boxes
     */
    public void drawChart(){

        chart.getData().clear();

        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("Norm values");

        XYChart.Series dataSeries2 = new XYChart.Series();
        dataSeries2.setName("Values for " + cityLabel.getText());

        String[] names = {"PM10 \u00B5g/m3","PM25 \u00B5g/m3","NO2 \u00B5g/m3","SO2 \u00B5g/m3","O3 \u00B5g/m3","CO mg/m3","BC \u00B5g/m3"};
        Integer[] values = {101,61,151,201,151,11,16};




        for(Stats s: statsList) {

            String name = s.getName().toUpperCase();
            double av = s.getAverage();

            if(name.equals("CO")) {
                name += " mg/m3";
                av = av/1000;
            }else
                name += " \u00B5g/m3"; //+ s.getUnit();



            dataSeries2.getData().add(new XYChart.Data(name, av));

            for (int i=0;i<names.length;i++) { //display only norm values for checked params

                if (name.equals(names[i]))
                    dataSeries1.getData().add(new XYChart.Data(names[i], values[i]));

            }

        }

        chart.getData().add(dataSeries1);
        chart.getData().add(dataSeries2);

    }

    /**
     * Handle checked parameters: create AirQ object and Stats object to get response
     * If there is no response show alert dialog
     * If there is a response display average value of parameter in text flow
     * @param param parameter got from check box
     * @return Stats object for further analyzing
     */
    public Stats handleParams(String param){

        String enteredCity = cityTxTField.getText().substring(0,1).toUpperCase() + cityTxTField.getText().substring(1).toLowerCase();
        String preparedParam = param.toLowerCase();


        if(preparedParam.contains("."))
            preparedParam = preparedParam.replace(".","");



        try {

                AirQ q = new AirQ(enteredCity, preparedParam);
                Stats stats = new Stats(q);

                Text n = new Text(stats.getName().toUpperCase() + "\n");
                Text t = new Text((df.format(stats.getAverage())) + " \u00B5g/m3 " + stats.getDate() + "\n\n");
                n.getStyleClass().add("n");
                txtFlow.getChildren().addAll(n,t);
                return stats;


        }catch (NoSuchElementException  e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("No " + param + " parameter for this city.");
            alert.show();
    }

        return null;
    }

    /**
     * Display statistics of chosen param in the combo box
     * @param stats Stats object od parameter
     */
    public void handleStats(Stats stats) {

        String val = paramsStatsComboBox.getValue().toLowerCase();

        if(val.contains("."))
            val = val.replace(".","");// for pm2.5 = pm25

        if (val.equals(stats.getName())) {

            nrMesLabel.setText(String.valueOf(stats.getNumberOfM()));
            maxVLabel.setText(df.format(stats.getMax()));
            minVLabel.setText(df.format(stats.getMin()));
            stDevLabel.setText(df.format(stats.getStDev()));
            aqiLabel.setText(stats.getAQI());
            handleAqiIndicator(stats.getAQI());
        }

    }

    /**
     * Move indicator on color line to accurate position on window
     * Changes the x position of indicator
     * The position is based on aqi level
     * @param aqiLvl AQI level calculated before
     */

    public void handleAqiIndicator(String aqiLvl){

        //based on calculated aqi level

        aqiIndicator.setManaged(false);

        String[] aqi = {"Very good","Good","Moderate","Sufficient","Bad","Very bad"};
        Integer[] x = {17,53,89,125,162,198}; //position of indicator


        for(int i=0;i<aqi.length;i++) {

            if(aqiLvl.equals(aqi[i]))
            aqiIndicator.setX(x[i]);
        }

    }


    /**
     * Get selected check boxes on list
     * @param boxes check boxes of parameters
     * @return list of names of selected check boxes
     */
    public List<String> handleCheckBoxes(CheckBox... boxes) {

        List<String> parameters = new ArrayList<>();

        for(CheckBox x: boxes){
            if(x.isSelected())
                parameters.add(x.getText());
        }

        return parameters;
    }

    /**
     * Clear forwarded checkboxes
     * @param boxes forwarded checkboxes
     */
    public void clearCheckBoxes(CheckBox... boxes){

        for(CheckBox c:boxes)
            c.setSelected(false);
    }

    /**
     * Clear all the labels forwarded
     * @param labels labels forwarded
     */
    public void clearLabels(Label... labels){

        for(Label l: labels)
            l.setText("");
    }

    /**
     * Set combo box items according to parameters in statsList
     */
    public void handleComboBox(){
        ObservableList<String> comboBox = FXCollections.observableArrayList();

        for(Stats stat: statsList){

            String string = stat.getName().toUpperCase();

            if (string.equals("PM25"))
                string = "PM2.5";

            comboBox.add(string);

        }

        paramsStatsComboBox.setItems(comboBox);
    }


    /**
     * Set ComboBox items of parameters to choose from for statistics
     */
    void setChoiceBox() {

        ObservableList<String> availableChoicesGrupa = FXCollections.observableArrayList("NO2","SO2","PM2.5","PM10","BC","CO","O3");

        paramsStatsComboBox.setItems(availableChoicesGrupa);


    }

    @FXML
    void initialize() {
        assert cityTxTField != null : "fx:id=\"cityTxTField\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert bcCheck != null : "fx:id=\"bcCheck\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert coCheck != null : "fx:id=\"coCheck\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert pm10Check != null : "fx:id=\"pm10Check\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert pm25Check != null : "fx:id=\"pm25Check\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert o3Check != null : "fx:id=\"o3Check\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert no2Check != null : "fx:id=\"no2Check\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert so2Check != null : "fx:id=\"so2Check\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert clearBtn != null : "fx:id=\"clearBtn\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert saveFileBtn != null : "fx:id=\"saveFileBtn\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert openFileBtn != null : "fx:id=\"openFileBtn\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert vBoxResults != null : "fx:id=\"vBoxResults\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert cityLabel != null : "fx:id=\"cityLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert paramsStatsComboBox != null : "fx:id=\"paramsStatsComboBox\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert showStatsBtn != null : "fx:id=\"showStatsBtn\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert nrMesLabel != null : "fx:id=\"nrMesLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert minVLabel != null : "fx:id=\"minVLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert maxVLabel != null : "fx:id=\"maxVLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert stDevLabel != null : "fx:id=\"stDevLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert aqiLabel != null : "fx:id=\"aqiLabel\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert aqiIndicator != null : "fx:id=\"aqiIndicator\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert txtFlow != null : "fx:id=\"txtFlow\" was not injected: check your FXML file 'airqualityapp.fxml'.";
        assert anchorTab != null : "fx:id=\"anchorTab\" was not injected: check your FXML file 'airqualityapp.fxml'.";

        setChoiceBox();
        clearCheckBoxes(bcCheck,coCheck,pm10Check,pm25Check,so2Check,no2Check,o3Check);
        vBoxResults.getStyleClass().add("vbox2");
        anchorTab.getStyleClass().add("vbox2");
        tab2.getStyleClass().add("tab2");
        txtFlow.getStyleClass().add("paragraph");
        saveFileBtn.setDisable(true);


    }
}
