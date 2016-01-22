package jone.helper.lib.model.network;

import android.content.Context;

import java.util.List;

/**
 * Created by jone.sun on 2016/1/20.
 */
public interface NetworkOperator {
    NetworkOperator init(Context context);
    <T>void request(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<T> responseCallback);
    <T>void requestOfList(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<List<T>> responseCallback);
    void cancel(String tag);
    void cancelAll();
}
