package jone.helper.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 对应百度API提供的天气
 * Created by jone.sun on 2015/2/9.
 */
public class Weather implements Serializable{
    private String currentCity;
    private String pm25;
    private List<WeatherData> weather_data;

    public Weather(){

    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public List<WeatherData> getWeather_data() {
        return weather_data;
    }

    public void setWeather_data(List<WeatherData> weather_data) {
        this.weather_data = weather_data;
    }
}
