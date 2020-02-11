package AirQualityApp;

import com.google.gson.Gson;

import java.time.LocalTime;

/**
 * Class for parsing the json of results for given param and city
 */
public class Measurement {

    private String location;
    private String parameter;
    private Object date;
    private double value;
    private String unit;
    private Object coordinates;
    private String country;
    private String city;

    public Measurement(String location, String parameter, Object date, double value, String unit, Object coordinates, String country, String city) {
        this.location = location;
        this.parameter = parameter;
        this.date = date;
        this.value = value;
        this.unit = unit;
        this.coordinates = coordinates;
        this.country = country;
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Object getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Object coordinates) {
        this.coordinates = coordinates;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public String toString() {
        return "Measurement{" +
                "location='" + location + '\'' +
                ", parameter='" + parameter + '\'' +
                ", date=" + date +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", coordinates=" + coordinates +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

}
