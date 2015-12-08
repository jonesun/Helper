package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * @author jone.sun on 2015/3/24.
 */
public class NetJSONObjectOperator extends NetBaseOperator<JSONObject, JSONObject> {
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
    public void request(String url, JSONObject params, NetResponseCallback<JSONObject> responseCallback) {
        addToRequest(new JsonObjectRequest(url,
                params,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void request(int method, String url, JSONObject params,
                        final NetResponseCallback<JSONObject> responseCallback) {
        addToRequest(new JsonObjectRequest(method,
                url,
                params,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void cancelAll() {
        cancelAll(TAG);
    }

}
