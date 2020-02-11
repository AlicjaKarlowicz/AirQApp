package AirQualityApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Tester {

    public static void main(String[] args) {

        AirQ q = new AirQ("Opole","pm10");
        Stats s = new Stats(q);
        System.out.println(s.getUnit());
        //System.out.println(s.getNumberOfM());

    }
}
