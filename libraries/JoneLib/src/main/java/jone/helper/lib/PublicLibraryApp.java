package jone.helper.lib;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import jone.helper.lib.model.imageCache.ImageCacheManager;
import jone.helper.lib.model.imageCache.RequestManager;
import jone.helper.lib.volley.VolleyCommon;

/**
 * 公共库Application
 * Created by jone.sun on 2015/12/8.
 */
public class PublicLibraryApp extends Application{
    public static final int WHAT_SHOW_TOAST = 1010101;
    private static PublicLibraryApp instance;
    private static Handler handler = new Handler();

    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private static Bitmap.CompressFormat DISK_IMAGE_CACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided

    public static PublicLibraryApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new ShowToastHandler(instance);

        RequestManager.init(getApplicationContext());
        createImageCache();
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache(){
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGE_CACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
    }

    static class ShowToastHandler extends Handler {
        private Context context;

        public ShowToastHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SHOW_TOAST:
                    if (msg.obj != null) {
                        String info = msg.obj.toString();
                        if (msg.arg1 == Toast.LENGTH_SHORT) {
                            Toast.makeText(context, info, Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(context, info, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static Handler getHandler() {
        return handler;
    }

    public static void showToast(String info) {
        showToast(info, Toast.LENGTH_SHORT);
    }

    public static void showToast(String info, int toastTime) {
        handler.sendMessage(handler.obtainMessage(WHAT_SHOW_TOAST, toastTime, 0, info));
    }
}
