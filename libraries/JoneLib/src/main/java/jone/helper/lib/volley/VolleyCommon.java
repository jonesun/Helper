package jone.helper.lib.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import jone.helper.lib.okhttp.SimpleOkHttpStack;

/**
 * @author jone.sun on 2015/3/24.
 */
public class VolleyCommon {
    public static final String TAG = VolleyCommon.class.getSimpleName();
    private static VolleyCommon instance = null;
    public static VolleyCommon getInstance(Context context){
        if(instance == null){
            instance = new VolleyCommon(context);
        }
        return instance;
    }

    private static RequestQueue mRequestQueue;
    private VolleyCommon(Context context) {
        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(context);
            mRequestQueue = Volley.newRequestQueue(context, new SimpleOkHttpStack()); //使用OkHttp
        }
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));//重试失败的请求，自定义请求超时
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        mRequestQueue.add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        mRequestQueue.add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void requestJsonObject(int method,
                                  String url,
                                  JSONObject jsonRequest,
                                  Response.Listener<JSONObject> successListener,
                                  Response.ErrorListener errorListener){
        addToRequestQueue(new JsonObjectRequest(method,
                url,
                jsonRequest,
                successListener,
                errorListener), "requestJsonObject");
    }

    public void requestString(int method,
                              String url,
                              final Map<String, String> params,
                              Response.Listener<String> successListener,
                              Response.ErrorListener errorListener){
        addToRequestQueue(new StringRequest(method,
                url,
                successListener,
                errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String data = new String(response.data, "GBK");
                    return Response.success(data,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }
        }, "requestString");
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }
}
