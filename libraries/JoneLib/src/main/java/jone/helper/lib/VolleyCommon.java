package jone.helper.lib;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by admin on 13-12-11.
 */
public class VolleyCommon {
    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;


    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public  VolleyCommon(Context context) {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        req.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));//重试失败的请求，自定义请求超时?
        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        mRequestQueue.add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        mRequestQueue.add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public void requestJsonObject(String url, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener){
        addToRequestQueue(new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                successListener,
                errorListener), "requestJsonObject");
    }
    public void requestString(String url, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        addToRequestQueue(new StringRequest(
                url,
                successListener,
                errorListener));
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }
}
