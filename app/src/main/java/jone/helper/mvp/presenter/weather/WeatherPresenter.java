package jone.helper.mvp.presenter.weather;

import android.content.Context;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface WeatherPresenter {
    void getWeather(Context context, String city);
}
