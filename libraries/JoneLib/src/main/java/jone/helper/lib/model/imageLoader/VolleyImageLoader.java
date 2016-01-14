package jone.helper.lib.model.imageLoader;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;

import jone.helper.lib.model.imageCache.ImageCacheManager;

/**
 * 采用Volley库的imageLoader
 * compile 'com.mcxiaoke.volley:library:1.0.19'
 * Created by jone.sun on 2016/1/12.
 */
public class VolleyImageLoader implements ImageLoader {
    private static VolleyImageLoader ourInstance = new VolleyImageLoader();

    public static VolleyImageLoader getInstance() {
        return ourInstance;
    }

    private VolleyImageLoader() {
    }
    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private static Bitmap.CompressFormat DISK_IMAGE_CACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
    public VolleyImageLoader init(Application application){
        ImageCacheManager.getInstance().init(application,
                application.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGE_CACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
        return this;
    }
    @Override
    public void display(Context context, ImageView imageView, String url, int image_loading, int image_error) {
        com.android.volley.toolbox.ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
        imageLoader.get(url, com.android.volley.toolbox.ImageLoader.getImageListener(
                imageView, image_loading, image_error));
    }

    @Override
    public void getBitmap(Context context, String url, final ImageLoaderListener imageLoaderListener) {
        ImageCacheManager.getInstance().getImageLoader().get(url, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                if(imageLoaderListener != null){
                    imageLoaderListener.onDone(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(imageLoaderListener != null){
                    imageLoaderListener.onDone(null);
                }
            }
        });
    }
}
