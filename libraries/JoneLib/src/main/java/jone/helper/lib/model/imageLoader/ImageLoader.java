package jone.helper.lib.model.imageLoader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by jone.sun on 2016/1/12.
 */
public interface ImageLoader {

    public void display(Context context, ImageView imageView, String url,
                               int image_loading, int image_error);

    public void getBitmap(Context context, String url,
                        final ImageLoaderListener imageLoaderListener);
}
