package jone.helper.ui.view;

import java.util.List;

import jone.helper.model.customAd.entity.CustomAdInfo;

/**
 * Created by jone.sun on 2015/7/20.
 */
public interface CustomAdView {
    void showLoading();
    void hideLoading();
    void showError(String reason);
    void setCustomAdInfoList(List<CustomAdInfo> customAdInfoList);
}
