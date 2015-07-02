package jone.helper.ui.view;

import jone.helper.model.weather.entity.Weather;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface WeatherView {
    void showLoading();
    void hideLoading();
    void showError(String reason);
    void setWeatherInfo(Weather weather);
}
