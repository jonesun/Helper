package jone.helper.model.customAd;

import android.content.Context;

/**
 * Created by jone.sun on 2015/7/20.
 */
public interface CustomAdInfoModel {
    void init(Context context);
    void loadData(Context context, OnCompleteListener onCompleteListener);
}
