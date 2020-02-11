package AirQualityApp;

/**
 * A class to determine air quality index
 * Based on given value and type of parameter
 */

public class AirQualityIndex {


    /**
     * Static method for setting aqi for PM10
     * @param value of PM10 parameter
     * @return aqi
     */
    public static String getAQIPM10(double value) {

        double[] range = {21,61,101,141,201};

        return getAirQualityIndex(value,range);
    }

    /**
     * Static method for setting aqi for PM25
     * @param value of PM25 parameter
     * @return aqi
     */
    public static String getAQIPM25(double value) {

        double[] range = {13,37,61,85,121};

        return getAirQualityIndex(value,range);
    }

    /**
     * Static method for setting aqi for O3
     * @param value of O3 parameter
     * @return aqi
     */

    public static String getAQIO3(double value) {

        double[] range = {71,121,151,181,241};

        return getAirQualityIndex(value,range);
    }

    /**
     * Static method for setting aqi for NO2
     * @param value of NO2 parameter
     * @return aqi
     */
    public static String getAQINO2(double value) {

        double[] range = {41,101,151,201,401};

        return getAirQualityIndex(value,range);
    }


    /**
     * Static method for setting aqi for SO2
     * @param value of SO2 parameter
     * @return aqi
     */
    public static String getAQISO2(double value) {

        double[] range = {51,101,201,351,501};

        return getAirQualityIndex(value,range);
    }


    /**
     * Static method for setting aqi for CO
     * @param value of CO parameter
     * @return aqi
     */
    public static String getAQICO(double value) {

        double[] range = {3000,7000,11000,15000,21000};

        return getAirQualityIndex(value,range);
    }

    /**
     * Static method for setting aqi for BC
     * @param value of BC parameter
     * @return aqi
     */
    public static String getAQIBC(double value){

        double[] range = {6,11,16,21,51};

        return getAirQualityIndex(value,range);
    }

    /**
     * Method for setting air quality index based on the value and its max values within intervals
     *  Unique for each parameter
     * @param value parameter value
     * @param rangeTab table of maximum val of each interval
     * @return
     */
    private static String getAirQualityIndex(double value, double[] rangeTab){

        String quality = "";


            if(range(value,0,rangeTab[0]))
                quality = "Very good";
            else if(range(value,rangeTab[0],rangeTab[1]))
                quality = "Good";
            else if(range(value,rangeTab[1],rangeTab[2]))
                quality = "Moderate";
            else if (range(value,rangeTab[2],rangeTab[3]))
                quality = "Sufficient";
            else if(range(value,rangeTab[3],rangeTab[4]))
                quality="Bad";
            else if(value>rangeTab[4])
                quality = "Very bad";


        return quality;
    }


    /**
     * Method for checking if value is within the given interval
     * Interval is open on the lower part and closed on the ypper part
     * @param value given value
     * @param min lower part of interval
     * @param max upper part of interval
     * @return true if value is within the interval
     */
    private static boolean range(double value, double min, double max){

        if (value > min && value <= max){
            return true;
        }else {
            return false;
        }
    }
}
