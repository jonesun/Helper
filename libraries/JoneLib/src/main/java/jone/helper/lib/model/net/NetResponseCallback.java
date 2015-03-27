package jone.helper.lib.model.net;

/**
 * @author jone.sun on 2015/3/25.
 */
public interface NetResponseCallback<T> {

    void onSuccess(T response);

    void onFailure(int statusCode, String error);
}
