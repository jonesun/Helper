package jone.net;

import android.content.Context;


import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author jone.sun on 2015/3/24.
 */
public class NetJSONObjectOperator extends NetBaseOperator<JSONObject> {
    private static final String TAG = NetJSONObjectOperator.class.getSimpleName();
    private static NetJSONObjectOperator instance;
    public static NetJSONObjectOperator getInstance(Context context){
        if(instance == null){
            instance = new NetJSONObjectOperator(context);
        }
        return instance;
    }
    private NetJSONObjectOperator(Context context){
        super(context);
    }

    private NetJSONObjectOperator(){
        super();
    }

    @Override
    public void request(String url, NetResponseCallback<JSONObject> responseCallback) {
        addToRequest(new JsonObjectRequest(url,
                null,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void request(String url, final Map<String, String> params, NetResponseCallback<JSONObject> responseCallback) {
        JSONObject jsonObject = new JSONObject(params);
        addToRequest(new JsonObjectRequest(url, jsonObject,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void request(int method, String url, final Map<String, String> params,
                        final NetResponseCallback<JSONObject> responseCallback) {
        JSONObject jsonObject = null;
        if(params != null){
            jsonObject = new JSONObject(params);
        }
        addToRequest(new JsonObjectRequest(method, url, jsonObject,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void cancelAll() {
        cancelAll(TAG);
    }

}
