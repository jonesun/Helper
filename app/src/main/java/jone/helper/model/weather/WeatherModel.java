package jone.helper.model.weather;

import android.content.Context;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface WeatherModel {
    void getLocationCity(Context context, OnLocationListener locationListener);
    void loadWeather(Context context, String city, OnWeatherListener listener);
}
