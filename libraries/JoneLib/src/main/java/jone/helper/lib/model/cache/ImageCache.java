package jone.helper.lib.model.cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by jone.sun on 2015/12/3.
 */
public class ImageCache {
    private ImageMemoryCache imageMemoryCache;
    private ImageFileCache imageFileCache;
    public ImageCache(Context context){
        imageMemoryCache = new ImageMemoryCache(context);
        imageFileCache = new ImageFileCache();
    }

    /***
     * 获得一张图片，从三个地方获取，首先内存缓存，然后文件缓存，最后是从网络获取
     * @param url
     * @return
     */
    public Bitmap getBitmap(String url){
        Bitmap result = imageMemoryCache.getBitmapFromCache(url);
        if(result == null){
            result = imageFileCache.getImage(url);
            if(result == null){
                result = ImageGetFromHttp.downloadBitmap(url);
                if(result != null){
                    imageFileCache.saveBitmap(result, url);
                    imageMemoryCache.addBitmapToCache(url, result);
                }
            }else {
                //添加到内存缓存
                imageMemoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }
}
