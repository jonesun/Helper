package jone.helper.lib.model.net;

import android.content.Context;

import com.android.volley.AuthFailureError;

import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

import jone.helper.lib.volley.customRequest.XMLRequest;

/**
 * @author jone.sun on 2015/4/3.
 */
public class NetXMLOperator extends NetBaseOperator<Map<String, String>, XmlPullParser> {
    private static final String TAG = NetXMLOperator.class.getSimpleName();
    private static NetXMLOperator instance;
    public static NetXMLOperator getInstance(Context context){
        if(instance == null){
            instance = new NetXMLOperator(context);
        }
        return instance;
    }
    private NetXMLOperator(Context context){
        super(context);
    }

    private NetXMLOperator(){
        super();
    }


    @Override
    public void request(String url, NetResponseCallback<XmlPullParser> responseCallback) {
        addToRequest(new XMLRequest(url,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)), TAG);
    }

    @Override
    public void request(String url, final Map<String, String> params, NetResponseCallback<XmlPullParser> responseCallback) {
        addToRequest(new XMLRequest(url,
                getSuccessListener(responseCallback), getErrorListener(responseCallback)){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        }, TAG);
    }

    @Override
    public void request(int method, String url, final Map<String, String> params, NetResponseCallback<XmlPullParser> responseCallback) {
        addToRequest(new XMLRequest(method, url,
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
