package jone.helper.model.bing;

import java.util.ArrayList;
/**
 * Created by jone.sun on 2015/10/16.
 */
public interface OnBingPicturesListener {
    void onSuccess(ArrayList<BingPicture> bingPictureList);
    void onError(String reason);
}
