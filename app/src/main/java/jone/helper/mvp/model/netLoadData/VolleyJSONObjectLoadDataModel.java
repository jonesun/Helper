package jone.helper.mvp.model.netLoadData;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jone.helper.App;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.mvp.model.load.Callback;
import jone.net.NetResponseCallback;

/**
 * Created by jone.sun on 2016/1/12.
 */
public class VolleyJSONObjectLoadDataModel extends VolleyBaseLoadDataModel<JSONObject> {
    @Override
    public void loadData(int method, String url, Map<String, String> params, final Callback<JSONObject> callback) {
        super.loadData(method, url, params, callback);
        NetworkRequest request = new NetworkRequest.Builder()
                .get()
                .params(params)
                .url(url)
                .setUsingCacheWithNoNetwork(true).build();
        App.getVolleyNetworkOperator().request(request, JSONObject.class, new ResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response, boolean fromCache) {
                if(callback != null){
                    callback.onComplete(Callback.RESULT_CODE_NORMAL, "success", response);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
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
