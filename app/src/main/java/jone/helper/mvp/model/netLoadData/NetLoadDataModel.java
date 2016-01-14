package jone.helper.mvp.model.netLoadData;


import java.util.Map;

import jone.helper.mvp.model.load.Callback;

/**
 * 网络加载数据model
 * Created by jone.sun on 2016/1/12.
 */
public interface NetLoadDataModel<T> {
    void loadData(int method, String url, Map<String, String> params, Callback<T> callback);
    void cancel();
}
