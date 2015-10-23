package jone.helper.mvp.view.weather;

import jone.helper.mvp.model.weather.entity.Weather;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface WeatherView {
    void showLoading();
    void hideLoading();
    void showError(String reason);
    void setWeatherInfo(Weather weather);
}
