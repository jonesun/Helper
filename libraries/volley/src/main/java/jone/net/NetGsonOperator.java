package jone.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.Map;
import java.lang.reflect.Type;

import jone.net.volley.VolleyCommon;
import jone.net.volley.customRequest.GsonRequest_;

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

    public <T>void request(int method, String url, Type type, final Map<String, String> params,
                        final NetResponseCallback<T> responseCallback) {
        volleyCommon.addToRequestQueue(new GsonRequest_<T>(method, url, type, null,
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
