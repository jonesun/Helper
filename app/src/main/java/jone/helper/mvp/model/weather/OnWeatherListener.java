package jone.helper.mvp.model.weather;

import jone.helper.mvp.model.weather.entity.Weather;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface OnWeatherListener {
    void onSuccess(Weather weather);
    void onError(String reason);
}
