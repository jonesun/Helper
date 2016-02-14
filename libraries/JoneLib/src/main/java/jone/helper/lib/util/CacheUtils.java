package jone.helper.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

//import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 缓存工具类
 * @author jone.sun on 2015/3/24.
 */
public class CacheUtils {
    private static final String TAG = CacheUtils.class.getSimpleName();

    private CacheUtils() {
        throw new AssertionError();
    }
    /***
     * 保存数据到缓存中
     *
     * @param context
     * @param fileName
     * @param data
     * @return
     */
    public static <T> boolean saveDataToCache(Context context, String fileName,
                                              T data) {
        boolean isSuccess = false;
        if (context != null && fileName != null && data != null) {
            File saveFile = getCacheFilePath(context, data, fileName);
            FileOutputStream outStream = null;
            try {
                if (saveFile.exists()) {
                    saveFile.delete();
                } else {
                    saveFile.getParentFile().mkdirs();
                }
                boolean createFileStatus = saveFile.createNewFile();
                if(!createFileStatus){
                    Log.e(TAG, "createFileStatus: " + createFileStatus);
                }
                outStream = new FileOutputStream(saveFile);
                if (data instanceof String) {
                    String s = (String) data;
                    outStream.write(s.getBytes());
                } else if (data instanceof Bitmap) {
                    Bitmap b = (Bitmap) data;
                    if (fileName.endsWith("jpg") || fileName.endsWith("JPG")
                            || fileName.endsWith("jpeg")
                            || fileName.endsWith("JPEG")) {
                        b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    } else {
                        b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    }
                    outStream.flush();
                }
                outStream.close();
                isSuccess = true;
            } catch (Exception e) {
                Log.e("CacheUtil", "saveDataToCache", e);
                return isSuccess;
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e1) {
                        Log.e("CacheUtil", "saveDataToCache", e1);
                        return isSuccess;
                    }
                }
            }
        }
        return isSuccess;
    }

    /***
     * 读取缓存数据
     *
     * @param context
     * @param fileName
     * @return
     */
//    public static String getDataFromCache(Context context, String fileName) {
//        return getDataFromCache(context, fileName, String.class);
//    }

    /***
     * 读取缓存数据
     *
     * @param context
     * @param fileName
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
//    public static <T> T getDataFromCache(Context context, String fileName,
//                                         Class<T> clazz) {
//        T data = null;
//        if (context != null && fileName != null) {
//            FileInputStream inStream = null;
//            try {
//                File readFile = getCacheFilePath(context, clazz, fileName);
//                if (readFile != null && readFile.exists()) {
//                    inStream = new FileInputStream(readFile);
//                    if (clazz.getCanonicalName().equals(
//                            String.class.getCanonicalName())) {
//                        int length = inStream.available();
//                        byte[] buffer = new byte[length];
//                        inStream.read(buffer);
//                        data = (T) EncodingUtils.getString(buffer, "UTF-8");
//                    } else if (clazz.getCanonicalName().equals(
//                            Bitmap.class.getCanonicalName())) {
//                        data = (T) BitmapFactory.decodeStream(inStream);
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("CacheUtil", "getDataFromCache", e);
//                return data;
//            } finally {
//                if (inStream != null) {
//                    try {
//                        inStream.close();
//                    } catch (IOException e) {
//                        Log.e("CacheUtil", "getDataFromCache", e);
//                        return data;
//                    }
//                }
//            }
//        }
//        return data;
//    }

    /***
     * 删除缓存文件
     *
     * @param context
     * @param clazz
     * @param fileName
     */
    public static <T> void deleteDataFromCache(Context context, Class<T> clazz,
                                               String fileName) {
        if (context != null && fileName != null) {
            File readFile = getCacheFilePath(context, clazz, fileName);
            if (readFile.exists()) {
                readFile.delete();
            }
        }
    }

    /***
     * 判断缓存文件是否存在
     *
     * @param context
     * @param clazz
     * @param fileName
     */
    public static <T> boolean isCacheFileExists(Context context,
                                                Class<T> clazz, String fileName) {
        boolean result = false;
        if (context != null && fileName != null) {
            File readFile = getCacheFilePath(context, clazz, fileName);
            if (readFile.exists()) {
                return true;
            }
        }
        return result;
    }

    /****
     * 最后修改时间
     *
     * @param context
     * @param clazz
     * @param fileName
     * @return
     */
    public static <T> long lastModified(Context context, Class<T> clazz,
                                        String fileName) {
        if (context != null && fileName != null) {
            File readFile = getCacheFilePath(context, clazz, fileName);
            if (readFile.exists()) {
                return readFile.lastModified();
            }
        }
        return 0;
    }

    /****
     * 检查缓存是否过期
     *
     * @param context
     * @param clazz
     * @param fileName
     * @param overdueTime
     * @return
     */
    public static <T> boolean isOverdue(Context context, Class<T> clazz,
                                        String fileName, long overdueTime) {
        long lastModifiedTime = lastModified(context, clazz, fileName);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastModifiedTime > overdueTime) {
            return true;
        }
        return false;
    }

    /***
     * 获取缓存文件的路径
     *
     * @param context
     * @param t
     * @param fileName
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> File getCacheFilePath(Context context, T t,
                                            String fileName) {
        String dirName = null;
        if (t instanceof Class) {
            dirName = ((Class) t).getSimpleName();
        } else {
            dirName = t == null ? null : t.getClass().getSimpleName();
        }
        return dirName == null ? new File(getDiskCacheDir(context)
                .getAbsolutePath() + File.separator + fileName) : new File(
                getDiskCacheDir(context).getAbsolutePath() + File.separator
                        + dirName + File.separator + fileName);
    }

    /***
     * 获取缓存的目录
     *
     * @param context
     * @return
     */
    public static File getDiskCacheDir(Context context) {
        File cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = getExternalCacheDir(context);
        } else {
            cachePath = context.getCacheDir();
        }
        return cachePath;
    }

    /***
     * 获取外部SD卡中的应用缓存路径
     *
     * @param context
     * @return
     */
    private static File getExternalCacheDir(Context context) {
        File path = context.getExternalCacheDir();
        if (path == null) {
            final String cacheDir = "/Android/data/" + context.getPackageName()
                    + "/cache/";
            path = new File(Environment.getExternalStorageDirectory().getPath()
                    + cacheDir);
        }
        return path;
    }


    /***
     * 下载文件到缓存目录
     * @param url
     * @param filename
     * @return
     */
    public static boolean downloadImageToCache(Context context, String url, String filename) {
        BufferedInputStream ins = null;
        FileOutputStream fos = null;
        URL u = null;
        try {
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.connect();
            conn.setConnectTimeout(6 * 1000);
            ins = new BufferedInputStream(conn.getInputStream());
            File flocal = getCacheFilePath(context, Bitmap.class, filename);
            if(flocal != null){
                if(flocal.exists()){
                    flocal.delete();
                }else {
                    File parentFile = flocal.getParentFile();
                    if(parentFile != null && !parentFile.exists()){
                        parentFile.mkdirs();
                    }
                }
            }
            if (flocal == null || !flocal.createNewFile()) {
                ins.close();
                return false;
            }
            fos = new FileOutputStream(flocal);
            byte[] buf = new byte[512];
            int count = -1;

            while ((count = ins.read(buf)) != -1) {
                fos.write(buf, 0, count);
            }
            fos.flush();
            fos.close();
            ins.close();

            if (flocal.length() == 0) {
                flocal.delete();
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "downloadImageToCache : " + url, e);
            return false;
        }  finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadImageToCache: " + url, e);
            }
        }
    }
}
