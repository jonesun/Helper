package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.Map;

import jone.helper.lib.volley.VolleyCommon;
import jone.helper.lib.volley.customRequest.GsonRequest;

/**
 * @author jone.sun on 2015/4/3.
 */
public class NetGsonOperator {
    private static final String TAG = NetGsonOperator.class.getSimpleName();
    private static NetGsonOperator instance;
    public static NetGsonOperator getInstance(Context context){
        if(instance == null){
            instance = new NetGsonOperator(context);
        }
        return instance;
    }

    private VolleyCommon volleyCommon;
    private NetGsonOperator(Context context){
        volleyCommon = VolleyCommon.getInstance(context);
    }

    private NetGsonOperator(){

    }

    public <T>void request(String url, Class<T> clazz, final NetResponseCallback<T> responseCallback) {
        volleyCommon.addToRequestQueue(new GsonRequest<T>(url, clazz,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseCallback != null) {
                            responseCallback.onSuccess(response);
                        }
                    }
                }, getErrorListener(responseCallback)), TAG);
    }

    public <T>void request(String url, Class<T> clazz, final Map<String, String> params, final NetResponseCallback<T> responseCallback) {
        volleyCommon.addToRequestQueue(new GsonRequest<T>(url, clazz,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseCallback != null) {
                            responseCallback.onSuccess(response);
                        }
                    }
                }, getErrorListener(responseCallback)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        }, TAG);
    }

    public <T>void request(int method, String url, Class<T> clazz, final Map<String, String> params,
                        final NetResponseCallback<T> responseCallback) {
        volleyCommon.addToRequestQueue(new GsonRequest<T>(method, url, clazz,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseCallback != null) {
                            responseCallback.onSuccess(response);
                        }
                    }
                }, getErrorListener(responseCallback)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        }, TAG);
    }

    private Response.ErrorListener getErrorListener(final NetResponseCallback responseCallback){
        return NetBaseOperator.getErrorListener(responseCallback);
    }

    public void cancelAll(String tag) {
        volleyCommon.cancelPendingRequests(tag);
    }
}
