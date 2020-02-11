package AirQualityApp;

import com.google.gson.Gson;


import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Class for keeping information of data in form of List of Measurement objects
 * List of Measurement contains different fields for the same air parameter measured in different locations in the city
 * Stats enables to perform task on parameter values such as getting average
 */
public class Stats  {

    /**
     * Object of AirQ class to send a request
     */
    private AirQ airq;
    /**
     * List gor from AirQ method request()
     */
    private List<Measurement> measurements;

    public Stats(AirQ airq)  {
        this.airq = airq;
        measurements = airq.request();
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * Method for getting parameter's name
     * @return parameters name
     */
    public  String getName(){

        return airq.getParameter();
    }


    public String getUnit(){
        return measurements.get(0).getUnit();

    }

    /**
     * Convert date String to desired format
     * Date is extracted form the first measurement on the list
     * @return desired date format
     */

    public String getDate(){

        String[] d = measurements.get(0).getDate().toString().split(",");

        String date = d[0].substring(7).replace("T"," ").replace("+","");
        date = date.substring(0,date.length()-5);

        return date;

    }

    /**
     * Method for defining AQI lvel
     * Based on the name of parameter it calls method from AirQualityIndex class
     * @return String of air quality index very good/good/bad etc.
     */
    public String getAQI() {

        String aqi="";

        if(getName().equals("pm10"))
            aqi =AirQualityIndex.getAQIPM10(getAverage());
        else if(getName().equals("pm25"))
            aqi =AirQualityIndex.getAQIPM25(getAverage());
        else if(getName().equals("no2"))
            aqi = AirQualityIndex.getAQINO2(getAverage());
        else if(getName().equals("co"))
            aqi = AirQualityIndex.getAQICO(getAverage());
        else if(getName().equals("so2"))
            aqi = AirQualityIndex.getAQISO2(getAverage());
        else if(getName().equals("o3"))
            aqi = AirQualityIndex.getAQIO3(getAverage());
        else if(getName().equals("bc"))
            aqi = AirQualityIndex.getAQIBC(getAverage());

        return aqi;

    }

    /**
     * Get number of measurements
     * @return measurements List size
     */
    public int getNumberOfM(){
        return measurements.size();
    }

    /**
     * Calculate average of measurements values
     * @return average
     */
    public double getAverage() {

       return measurements.stream()
                .map(Measurement::getValue)
                .mapToDouble((x) -> x)
                .average()
                .getAsDouble();
    }


    /**
     * Calculate standard deviation based on average calculations
     * @return standard deviation
     */
    public double getStDev(){

        double sum =  measurements.stream()
                .map(Measurement::getValue)
                .mapToDouble((x) -> x)
                .map(x -> Math.pow(x - getAverage(),2))
                .sum();


        return Math.sqrt(sum/(measurements.size()-1));
    }

    /**
     * Get minimum value of parameter in measurement List
     * @return minimum value
     */
    public double getMin() {
        return measurements.stream()
                .map(Measurement::getValue)
                .min(Comparator.comparing(x -> x))
                .get();
    }

    /**
     * Get maximum value of parameter in measurements List
     * @return maximum value
     */
    public double getMax() {
        return measurements.stream()
                .map(Measurement::getValue)
                .max(Comparator.comparing(x -> x))
                .get();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);

    }

}
