package jone.net;

import java.util.Map;

/**
 * Created by jone.sun on 2015/3/25.
 */
public interface NetOperator<T> {
    public void request(String url, NetResponseCallback<T> responseCallback);
    public void request(String url, Map<String, String> params, NetResponseCallback<T> responseCallback);
    public void request(int method, String url, Map<String, String> params, NetResponseCallback<T> responseCallback);
    public void cancelAll();
}
