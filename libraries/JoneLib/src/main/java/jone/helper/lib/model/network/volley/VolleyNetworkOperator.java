package jone.helper.lib.model.network.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;

import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.net.volley.customRequest.CachingStringRequest;
import jone.net.volley.customRequest.CustomJsonObjectRequest;

/**
 * Volley方式请求
 * Created by jone.sun on 2016/1/26.
 */
public class VolleyNetworkOperator extends BaseVolleyNetworkOperator{
    public static final String TAG = "VolleyNetworkOperator";
    private static VolleyNetworkOperator mInstance;
    public static VolleyNetworkOperator getInstance() {
        if (mInstance == null) {
            synchronized (VolleyNetworkOperator.class) {
                if (mInstance == null) {
                    mInstance = new VolleyNetworkOperator();
                }
            }
        }
        return mInstance;
    }
    private VolleyNetworkOperator(){}

    @Override
    public void StringGetRequest(final NetworkRequest networkRequest, ResponseCallback<String> responseCallback) {
        stringRequest(Request.Method.GET, networkRequest, responseCallback);
    }

    @Override
    public void StringPostRequest(final NetworkRequest networkRequest, ResponseCallback<String> responseCallback) {
        stringRequest(Request.Method.POST, networkRequest, responseCallback);
    }

    @Override
    public void JSONObjectGetRequest(NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback) {
        jsonObjectRequest(Request.Method.GET, networkRequest, responseCallback);
    }

    @Override
    public void JSONObjectPostRequest(NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback) {
        jsonObjectRequest(Request.Method.POST, networkRequest, responseCallback);
    }

    private void stringRequest(int method, final NetworkRequest networkRequest, ResponseCallback<String> responseCallback){
        StringRequest request = new CachingStringRequest(method, networkRequest.getUrl(),
                getSuccessListener(networkRequest, System.currentTimeMillis(), responseCallback),
                getErrorListener(networkRequest, System.currentTimeMillis(), responseCallback),
                networkRequest.isUsingCacheWithNoNetwork()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return networkRequest.getParams();
            }
        };
        addToRequestQueue(request, networkRequest);
    }

    private void jsonObjectRequest(int method, NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback){
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(method, networkRequest.getUrl(),networkRequest.getParams(),
                getSuccessListener(networkRequest, System.currentTimeMillis(), responseCallback),
                getErrorListener(networkRequest, System.currentTimeMillis(), responseCallback),
                networkRequest.isUsingCacheWithNoNetwork());
        addToRequestQueue(request, networkRequest);
    }
}
