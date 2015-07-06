package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import jone.helper.lib.volley.VolleyCommon;
import jone.helper.lib.volley.VolleyErrorHelper;

/**
 * @author jone.sun on 2015/3/24.
 */
public class NetJSONObjectOperator implements NetOperator<JSONObject, JSONObject> {
    private VolleyCommon volleyCommon;
    private static NetJSONObjectOperator instance;
    public static NetJSONObjectOperator getInstance(Context context){
        if(instance == null){
            instance = new NetJSONObjectOperator(context);
        }
        return instance;
    }
    private NetJSONObjectOperator(Context context){
        volleyCommon = VolleyCommon.getInstance(context);
    }
    private NetJSONObjectOperator(){}

    @Override
    public void request(int method, String url, JSONObject params,
                        final NetResponseCallback<JSONObject> responseCallback) {
        volleyCommon.requestJsonObject(method,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(responseCallback != null){
                            responseCallback.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo 先把VolleyError转换成String
                        if(responseCallback != null){
                            responseCallback.onFailure(VolleyErrorHelper.getMessage(error));
                        }
                    }
                });
    }
}
