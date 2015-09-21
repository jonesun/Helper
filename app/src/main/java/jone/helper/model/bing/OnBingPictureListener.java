package jone.helper.model.bing;

/**
 * Created by jone.sun on 2015/7/2.
 */
public interface OnBingPictureListener {
    void onSuccess(BingPicture bingPicture);
    void onError(String reason);
}
