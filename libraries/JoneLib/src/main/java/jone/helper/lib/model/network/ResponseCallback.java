package jone.helper.lib.model.network;

/**
 * Created by jone.sun on 2016/1/20.
 */
public interface ResponseCallback<T> {
    void onSuccess(T response, boolean fromCache);

    void onFailure(Exception e);
}
