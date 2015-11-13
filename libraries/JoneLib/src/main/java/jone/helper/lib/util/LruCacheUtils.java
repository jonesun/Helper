package jone.helper.lib.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * 内存缓存图片
 * Created by jone.sun on 2015/11/6.
 */
public class LruCacheUtils {
    private static final String TAG = LruCacheUtils.class.getSimpleName();
    private int MAX_MEMORY = (int) (Runtime.getRuntime() .maxMemory() / 1024);
    private LruCache<String, Bitmap> mMemoryCache;
    private LruCacheUtils() {
        if (mMemoryCache == null)
            mMemoryCache = new LruCache<String, Bitmap>(
                    MAX_MEMORY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {
                    Log.v(TAG, "hard cache is full , push to soft cache");
                }
            };
    }

    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(mMemoryCache.get(key) == null){
            if(key != null && bitmap != null){
                mMemoryCache.put(key, bitmap);
            }
        }else {
            Log.w(TAG, "the res is already exits");
        }
    }

    public synchronized Bitmap getBitmapFromMenCache(String key){
        if(key != null){
            return mMemoryCache.get(key);
        }
        return null;
    }

    public synchronized void removeImageCache(String key){
        if(key != null){
            if(mMemoryCache != null){
                Bitmap bitmap = mMemoryCache.remove(key);
                if(bitmap != null){
                    bitmap.recycle();
                }
            }
        }
    }

    public void clearCache(){
        if(mMemoryCache != null){
            if(mMemoryCache.size() > 0){
                Log.d(TAG, "clearCache start>>mMemoryCache.size() = " + mMemoryCache.size());
                mMemoryCache.evictAll();
                Log.d(TAG, "clearCache end>>mMemoryCache.size() = " + mMemoryCache.size());
            }
            mMemoryCache = null;
        }
    }
}
