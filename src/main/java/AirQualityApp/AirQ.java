package AirQualityApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class for connecting with url request
 * Recieving information from api on curren air parameters
 * And transfering this information further
 */
public class AirQ {

    /**
     * City for which the user want to reasearch air quality
     */
    private String city;

    /**
     * Parameter of air quality such as pm10, pm2.5
     */
    private String parameter;

    public AirQ(String city, String parameter) {
        this.city = city;
        this.parameter = parameter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Private method for URL encoding in order to get information also for Polish cities
     * @param value value that needs to be encoded
     * @return encoded String value
     * @throws UnsupportedEncodingException
     */
    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

    }


    /**
     * Connects to api by URL for given city and parameter for first 20 results
     * Parsing response from URL to JSON object to get desired results
     * Creating list of Measurements objects, filtering distnict locations
     * @return list of Measurements objects
     */
    public List<Measurement> request()  {


        StringBuffer response = new StringBuffer();
        StringBuffer urlBuffer = new StringBuffer();

        try {

            urlBuffer.append("https://api.openaq.org/v1/measurements?city=");

            urlBuffer.append(encode(city));

            urlBuffer.append("&parameter=");
            urlBuffer.append(encode(parameter));
            urlBuffer.append("&limit=20");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        String url = urlBuffer.toString();


        try {

            URL obj = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {

            System.out.println("bad url");

        } catch (IOException e) {

            System.out.println("Connection failed");
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();


            Map<String, Object> m = null;

            m = gson.fromJson(response.toString(), Map.class);

        List<Object> resultsList = (List<Object>) m.get("results");


        List<Measurement> measurementList = new ArrayList<>();

        for (Object o : resultsList) {

            String resultsJson = gson.toJson(o);

            Measurement measurement = null;


                measurement = gson.fromJson(resultsJson, Measurement.class);


            measurement.getLocation();
            measurementList.add(measurement);
        }


       measurementList=  measurementList.stream()
                .filter(distinctByKey(Measurement::getLocation))
                .collect(Collectors.toList());


        return measurementList;
    }

    /**
     * Predictate to get distinct locations from List of Measurements
     * Searches by distnict key in filter stream method
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    @Override
    public String toString() {
        return "AirQ{" +
                "city='" + city + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
