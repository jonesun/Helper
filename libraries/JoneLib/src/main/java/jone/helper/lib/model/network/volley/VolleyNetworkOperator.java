package jone.helper.lib.model.network.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import jone.helper.lib.model.network.NetworkOperator;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.net.volley.customRequest.CachingStringRequest;
import jone.net.volley.customRequest.CustomJsonObjectRequest;

/**
 * Created by jone.sun on 2016/1/21.
 */
public class VolleyNetworkOperator implements NetworkOperator {
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
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private RequestQueue mRequestQueue;
    @Override
    public NetworkOperator init(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context, cacheSize);
//            mRequestQueue = Volley.newRequestQueue(context, new SimpleOkHttpStack()); //使用OkHttp
        }
        return this;
    }

    @Override
    public <T> void request(NetworkRequest networkRequest, Class<T> clazz, final ResponseCallback<T> responseCallback) {
        if(networkRequest == null){
            throw new RuntimeException("networkRequest cannot null");
        }
        String url = networkRequest.getUrl();
        if(url == null){
            throw new RuntimeException("url cannot null");
        }
        if(clazz.getName().equals(String.class.getName())){
            StringRequest request = initStringRequest(networkRequest, new ResponseCallback<String>() {
                @Override
                public void onSuccess(String response, boolean fromCache) {
                    if(responseCallback != null){
                        responseCallback.onSuccess((T) response, fromCache);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if(responseCallback != null){
                        responseCallback.onFailure(e);
                    }
                }
            });
            addToRequest(request, getTag(networkRequest), networkRequest.isUsingCacheWithNoNetwork());
        }else if(clazz.getName().equals(JSONObject.class.getName())){
            Request<JSONObject> request = initJsonObjectRequest(networkRequest, new ResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response, boolean fromCache) {
                    if(responseCallback != null){
                        responseCallback.onSuccess((T) response, fromCache);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if(responseCallback != null){
                        responseCallback.onFailure(e);
                    }
                }
            });
            addToRequest(request, getTag(networkRequest), networkRequest.isUsingCacheWithNoNetwork());
        }
        else {
            throw new RuntimeException(clazz.getName() + " has not support!");
        }
    }

    @Override
    public <T> void requestOfList(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<List<T>> responseCallback) {

    }

    @Override
    public void cancel(String tag) {
        mRequestQueue.cancelAll(tag);
    }

    @Override
    public void cancelAll() {
        mRequestQueue.cancelAll(null);
    }

    public <T>void addToRequest(Request<T> request, String tag, boolean shouldCache) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        request.setShouldCache(shouldCache);
        request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));//重试失败的请求，自定义请求超时
        VolleyLog.e("Adding request to queue: %s", request.getUrl());
        mRequestQueue.add(request);
    }


    private StringRequest initStringRequest(final NetworkRequest networkRequest, ResponseCallback<String> responseCallback){
        int method = networkRequest.getMethod();
        StringRequest stringRequest = null;
        if(method == NetworkRequest.Method.GET){
            stringRequest = new CachingStringRequest(networkRequest.getUrl(),
                    getSuccessListener(responseCallback), getErrorListener(responseCallback), networkRequest.isUsingCacheWithNoNetwork()){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return networkRequest.getParams();
                }
            };
        }else if(method == NetworkRequest.Method.POST){
            stringRequest = new CachingStringRequest(Request.Method.POST, networkRequest.getUrl(),
                    getSuccessListener(responseCallback), getErrorListener(responseCallback), networkRequest.isUsingCacheWithNoNetwork()){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return networkRequest.getParams();
                }
            };
        }else {
            if (responseCallback != null) {
                responseCallback.onFailure(new Exception(method + " this is not support!"));
            }
        }
        return stringRequest;
    }

    private CustomJsonObjectRequest initJsonObjectRequest(final NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback){
        int method = networkRequest.getMethod();
        CustomJsonObjectRequest request = null;
        if(method == NetworkRequest.Method.GET){
            request = new CustomJsonObjectRequest(networkRequest.getUrl(),networkRequest.getParams(),
                    getSuccessListener(responseCallback), getErrorListener(responseCallback),
                    networkRequest.isUsingCacheWithNoNetwork());
        }else if(method == NetworkRequest.Method.POST){
            request = new CustomJsonObjectRequest(Request.Method.POST, networkRequest.getUrl(),networkRequest.getParams(),
                    getSuccessListener(responseCallback), getErrorListener(responseCallback),
                    networkRequest.isUsingCacheWithNoNetwork());
        }else {
            if (responseCallback != null) {
                responseCallback.onFailure(new Exception(method + " this is not support!"));
            }
        }
        return request;
    }

    protected <T>Response.Listener<T> getSuccessListener(final ResponseCallback<T> responseCallback){
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (responseCallback != null) {
                    responseCallback.onSuccess(response, false);
                }
            }
        };
    }

    protected static Response.ErrorListener getErrorListener(final ResponseCallback responseCallback){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (responseCallback != null) {
                    responseCallback.onFailure(new Exception(VolleyErrorHelper.getMessage(error)));
                }
            }
        };
    }

    private String getTag(NetworkRequest networkRequest){
        String tag = networkRequest.getTag();
        if(TextUtils.isEmpty(tag)){
            tag = networkRequest.getUrl(); //如果tag为空则设置为url
        }
        return tag;
    }
}
