package jone.helper.mvp.model.netLoadData;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.Map;

import jone.helper.App;
import jone.helper.lib.model.net.NetResponseCallback;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.SystemUtil;
import jone.helper.mvp.model.load.Callback;

/**
 * 用Volley的GsonRequest的方式加载数据的model
 * Created by jone.sun on 2016/1/12.
 */
public class VolleyGsonLoadDataModel<T> extends VolleyBaseLoadDataModel<T> {
    @Override
    public void loadData(int method, String url, Map<String, String> params, final Callback<T> callback) {
        super.loadData(method, url, params, callback);
        App.getNetGsonOperator().request(method, url, new TypeToken<T>(){}.getType(), params,
                new NetResponseCallback<T>() {
                    @Override
                    public void onSuccess(T response) {
//                        Log.e("VolleyGsonLoadDataModel", "response: " + GsonUtils.toJson(response));
                        if(callback != null){
                            callback.onComplete(Callback.RESULT_CODE_NORMAL, "success", response);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
//                        Log.e("VolleyGsonLoadDataModel", "error: " + error);
                        if(callback != null){
                            callback.onComplete(Callback.RESULT_CODE_SERVER_ERROR, "error: " + error, null);
                        }
                    }
                });
    }

    @Override
    public void cancel() {
        App.getNetGsonOperator().cancelAll(TAG);
    }
}
