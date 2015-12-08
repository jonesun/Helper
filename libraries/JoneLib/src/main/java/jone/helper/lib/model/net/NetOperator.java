package jone.helper.lib.model.net;

/**
 * Created by jone.sun on 2015/3/25.
 */
public interface NetOperator<E, T> {
    public void request(String url, NetResponseCallback<T> responseCallback);
    public void request(String url, E params, NetResponseCallback<T> responseCallback);
    public void request(int method, String url, E params, NetResponseCallback<T> responseCallback);
    public void cancelAll();
}
