package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import jone.helper.lib.volley.VolleyCommon;
import jone.helper.lib.volley.VolleyErrorHelper;

/**
 * @author jone.sun on 2015/4/3.
 */
public abstract class NetBaseOperator<T> implements NetOperator<T> {
    private VolleyCommon volleyCommon;
    protected NetBaseOperator(Context context){
        volleyCommon = VolleyCommon.getInstance(context);
    }
    protected NetBaseOperator(){}

    protected Response.Listener<T> getSuccessListener(final NetResponseCallback<T> responseCallback){
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (responseCallback != null) {
                    responseCallback.onSuccess(response);
                }
            }
        };
    }

    protected static Response.ErrorListener getErrorListener(final NetResponseCallback responseCallback){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (responseCallback != null) {
                    responseCallback.onFailure(VolleyErrorHelper.getMessage(error));
                }
            }
        };
    }

    public void addToRequest(Request<T> request, String tag) {
        volleyCommon.addToRequestQueue(request, tag);
    }

    public void cancelAll(String tag) {
        volleyCommon.cancelPendingRequests(tag);
    }
}
