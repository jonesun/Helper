package jone.helper.model.bing;

import java.util.List;

/**
 * Created by jone.sun on 2015/10/16.
 */
public interface OnBingPicturesListener {
    void onSuccess(List<BingPicture> bingPictureList);
    void onError(String reason);
}
