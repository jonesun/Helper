package jone.helper.mvp.model.netLoadData;

import java.util.Map;

import jone.helper.App;
import jone.helper.lib.util.SystemUtil;
import jone.helper.mvp.model.load.Callback;

/**
 * Created by jone.sun on 2016/1/14.
 */
public abstract class VolleyBaseLoadDataModel<T> implements NetLoadDataModel<T> {
    public static final String TAG = "VolleyLoadDataModel";
    @Override
    public void loadData(int method, String url, Map<String, String> params, Callback<T> callback) {
        if(!SystemUtil.hasNetWork(App.getInstance())){
            if(callback != null){
                callback.onComplete(Callback.RESULT_CODE_NO_NETWORK, "no network", null);
            }
            return;
        }
    }

}
