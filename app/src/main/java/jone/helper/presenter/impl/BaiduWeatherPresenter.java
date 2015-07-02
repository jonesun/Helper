package jone.helper.presenter.impl;

import android.content.Context;

import jone.helper.model.weather.OnLocationListener;
import jone.helper.model.weather.OnWeatherListener;
import jone.helper.model.weather.WeatherModel;
import jone.helper.model.weather.entity.Weather;
import jone.helper.model.weather.impl.BaiduWeatherModel;
import jone.helper.presenter.WeatherPresenter;
import jone.helper.ui.view.WeatherView;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class BaiduWeatherPresenter implements WeatherPresenter, OnWeatherListener {
    /*Presenter作为中间层，持有View和Model的引用*/
    private WeatherView weatherView;
    private WeatherModel weatherModel;

    public BaiduWeatherPresenter(WeatherView weatherView) {
        this.weatherView = weatherView;
        weatherModel = new BaiduWeatherModel();
    }

    @Override
    public void getWeather(Context context, String city) {
        weatherView.showLoading();
        weatherModel.loadWeather(context, city, this);
    }

    @Override
    public void onSuccess(Weather weather) {
        weatherView.hideLoading();
        weatherView.setWeatherInfo(weather);
    }

    @Override
    public void onError(String reason) {
        weatherView.hideLoading();
        weatherView.showError(reason);
    }
}
