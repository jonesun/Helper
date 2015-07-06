package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

import jone.helper.lib.volley.VolleyCommon;
import jone.helper.lib.volley.VolleyErrorHelper;


/**
 * @author jone.sun on 2015/3/24.
 */
public class NetStringOperator implements NetOperator<Map<String, String>, String> {
    private VolleyCommon volleyCommon;
    private static NetStringOperator instance;
    public static NetStringOperator getInstance(Context context){
        if(instance == null){
            instance = new NetStringOperator(context);
        }
        return instance;
    }
    private NetStringOperator(Context context){
        volleyCommon = VolleyCommon.getInstance(context);
    }

    private NetStringOperator(){}

    @Override
    public void request(int method, String url, Map<String, String> params,
                           final NetResponseCallback<String> responseCallback) {
        volleyCommon.requestString(method,
                url,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
