package jone.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * @author jone.sun on 2015/3/24.
 */
public class NetStringOperator extends NetBaseOperator<String>{
    private static final String TAG = NetStringOperator.class.getSimpleName();
    private static NetStringOperator instance;
    public static NetStringOperator getInstance(Context context){
        if(instance == null){
            instance = new NetStringOperator(context);
        }
        return instance;
    }
    public NetStringOperator(Context context){
        super(context);
    }

    private NetStringOperator(){
        super();
    }

    @Override
    public void request(String url, NetResponseCallback<String> responseCallback) {
        addToRequest(new StringRequest(url,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void request(String url, final Map<String, String> params, NetResponseCallback<String> responseCallback) {
        addToRequest(new StringRequest(url,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        }, TAG);
    }

    @Override
    public void request(int method, String url, final Map<String, String> params,
                           NetResponseCallback<String> responseCallback) {
        addToRequest(new StringRequest(method, url,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        }, TAG);
    }

    @Override
    public void cancelAll() {
        cancelAll(TAG);
    }
}
