package jone.helper.presenter.customAd.impl;

import android.content.Context;

import java.util.List;

import jone.helper.model.customAd.CustomAdInfoModel;
import jone.helper.model.customAd.OnCompleteListener;
import jone.helper.model.customAd.entity.CustomAdInfo;
import jone.helper.model.customAd.impl.JoneWPCustomAdInfoModel;
import jone.helper.model.weather.OnWeatherListener;
import jone.helper.model.weather.WeatherModel;
import jone.helper.model.weather.entity.Weather;
import jone.helper.model.weather.impl.BaiduWeatherModel;
import jone.helper.presenter.customAd.CustomAdPresenter;
import jone.helper.presenter.weather.WeatherPresenter;
import jone.helper.ui.view.CustomAdView;
import jone.helper.ui.view.WeatherView;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class JoneWPCustomPresenter implements CustomAdPresenter, OnCompleteListener {
    /*Presenter作为中间层，持有View和Model的引用*/
    private CustomAdView view;
    private CustomAdInfoModel model;

    public JoneWPCustomPresenter(Context context, CustomAdView view) {
        this.view = view;
        model = new JoneWPCustomAdInfoModel();
        model.init(context);
    }

    @Override
    public void getCustomAdList(Context context) {
        view.showLoading();
        model.loadData(context, this);
    }

    @Override
    public void onComplete(List<CustomAdInfo> customAdInfoList) {
        view.hideLoading();
        if(customAdInfoList != null && customAdInfoList.size() > 0){
            view.setCustomAdInfoList(customAdInfoList);
        }else {
            view.showError("list in null");
        }
    }
}
