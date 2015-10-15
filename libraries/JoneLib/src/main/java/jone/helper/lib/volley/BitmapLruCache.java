package jone.helper.lib.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by jone.sun on 2015/10/15.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public BitmapLruCache(int maxSize) {
        super(maxSize);
        initLocalFileManager();
    }

    private void initLocalFileManager() {

    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        // TODO value.getByteCount();
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap tbm = get(url);
        if (tbm != null) {
            return tbm;
        }
        return null; //TODO local file
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}