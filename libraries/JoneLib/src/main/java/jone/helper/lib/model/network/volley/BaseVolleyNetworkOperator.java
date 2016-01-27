package jone.helper.lib.model.network.volley;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import jone.helper.lib.model.network.NetworkBaseOperator;
import jone.helper.lib.model.network.NetworkOperator;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.lib.util.SystemUtil;

/**
 * Created by jone.sun on 2016/1/26.
 */
public abstract class BaseVolleyNetworkOperator extends NetworkBaseOperator {
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private Context context;
    private RequestQueue mRequestQueue;
    @Override
    public NetworkOperator init(Context context) {
        this.context = context;
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context, cacheSize);
//            mRequestQueue = Volley.newRequestQueue(context, new SimpleOkHttpStack()); //使用OkHttp
        }
        return this;
    }

    @Override
    public void cancel(String tag) {
        mRequestQueue.cancelAll(tag);
    }

    @Override
    public void cancelAll() {
        mRequestQueue.cancelAll(null); //todo 取消全部请求
    }

    protected <T>Response.Listener<T> getSuccessListener(final NetworkRequest networkRequest, final long startTime, final ResponseCallback<T> responseCallback){
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (responseCallback != null) {
                    responseCallback.onSuccess(response, 
                            !SystemUtil.hasNetWork(context)); //// TODO: 2016/1/26 后期优化是否来自缓存的方法判断 
                }
                if(networkRequest.isWriteDebugLog()){
                    Log.e("VolleyNetworkOperator", "request>>success: " + networkRequest.getUrl() + "    耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                }
            }
        };
    }

    protected static Response.ErrorListener getErrorListener(final NetworkRequest networkRequest, final long startTime, final ResponseCallback responseCallback){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (responseCallback != null) {
                    responseCallback.onFailure(new Exception(VolleyErrorHelper.getMessage(error)));
                }
                if(networkRequest.isWriteDebugLog()){
                    Log.e("VolleyNetworkOperator", "request>>error: " + networkRequest.getUrl() + "    耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                }
            }
        };
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, NetworkRequest networkRequest) {
        req.setTag(networkRequest.getTag());
        req.setShouldCache(networkRequest.isUsingCacheWithNoNetwork());
        req.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));//重试失败的请求，自定义请求超时
        if(networkRequest.isWriteDebugLog()){
            VolleyLog.d("Adding request to queue: %s", req.getUrl());
        }
        mRequestQueue.add(req);
    }
}

