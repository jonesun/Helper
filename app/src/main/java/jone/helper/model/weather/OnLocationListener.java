package jone.helper.model.weather;


/**
 * Created by jone.sun on 2015/7/2.
 */
public interface OnLocationListener {
    void onSuccess(String city);
    void onError(String reason);
}
