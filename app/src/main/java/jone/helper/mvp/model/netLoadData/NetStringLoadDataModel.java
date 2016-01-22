package jone.helper.mvp.model.netLoadData;

import android.util.Log;

import java.util.Map;

import jone.helper.App;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.mvp.model.load.Callback;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class NetStringLoadDataModel implements NetLoadDataModel<String> {
    @Override
    public void loadData(int method, String url, Map<String, String> params, final Callback<String> callback) {
        NetworkRequest request = new NetworkRequest.Builder()
                .get()
                .url(url)
                .params(params)
                .setUsingCacheWithNoNetwork(true).build();
        App.getVolleyNetworkOperator().request(request, String.class, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String response, boolean fromCache) {
                Log.e("NetStringLoadDataModel", fromCache + "volleyNetworkOperator>>onSuccess: " + response);
                if (callback != null) {
                    callback.onComplete(Callback.RESULT_CODE_NORMAL, "success", response);
                }
            }

            @Override
            public void onFailure(Exception e) {
//                Log.e(TAG, "volleyNetworkOperator>>onFailure: " + e.getMessage());
                if (callback != null) {
                    callback.onComplete(Callback.RESULT_CODE_SERVER_ERROR, "error: " + e.getMessage(), null);
                }
            }
        });
    }

    @Override
    public void cancel() {
        App.getVolleyNetworkOperator().cancelAll();
    }
}
