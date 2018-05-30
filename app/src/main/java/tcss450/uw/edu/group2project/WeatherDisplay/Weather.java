package tcss450.uw.edu.group2project.WeatherDisplay;

import java.io.Serializable;

public class Weather implements Serializable {
    private String zip;
    private Double lon;
    private Double lat;
    private String city;
    private String state;
    private String currWeather;
    private String farTemp;
    private String minTemp;
    private String maxTemp;
    private String cityState;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCityState() { return cityState; }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }

    public String getCurrWeather() {
        return currWeather;
    }

    public void setCurrWeather(String currWeather) {
        this.currWeather = currWeather;
    }


    public String getFarTemp() {
        return farTemp;
    }

    public void setFarTemp(String farTemp) {
        this.farTemp = farTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }


}
