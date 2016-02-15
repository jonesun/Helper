package jone.helper.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2014/12/24.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static String  long2string(long time,SimpleDateFormat dateFormat){
        return dateFormat.format(time);
    }

    public static String formatDataTime(long time, String format) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    public static String weekNum2string(int weekNum){
        String[] weekStrings = {"日", "一", "二", "三", "四", "五", "六"};
        return weekStrings[weekNum - 1];
    }

    /**
     * 判断网络情况
     *
     * @return false 表示没有网络 true 表示有网络
     */
    public static boolean isNetworkAlive(Context context) {
        return SystemUtil.hasNetWork(context);
    }

    public static File getSharedPicFile(Context context) {
        setNomedia();
        File file = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "jone_helper"+ File.separator +"jone_helper_shared.png");
        if(!file.exists()){
            File path = file.getParentFile();
            if(!path.exists()){
                path.mkdirs();
            }
            try {
                copyFromAssets(context, "jone_helper_shared.png", file.getPath());
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        return file;
    }

    public static void copyFromAssets(Context context, String assetPath,
                                      String tempPath) throws Exception {
        InputStream inputStream = context.getAssets().open(assetPath);
        FileOutputStream fileOutputStream = new FileOutputStream(tempPath);
        int length = -1;
        byte[] buffer = new byte[0x400000];
        while ((length = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
    }

    public static void setNomedia() {
        File file = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "jone_helper"+ File.separator + ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
               Log.e(TAG, "setNomedia", e);
            }
        }
    }

    /**
     * 根据设备类型设置屏幕方向
     * @param activity
     */
    public static void setScreenOrientation(Activity activity){
        if(SystemUtil.isPad(activity)){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void hideStartActivity(Context context, String url){
        Uri uri = Uri.parse(url);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        //先验证是否有App可以接收这个Intent，否则可能会奔溃
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent,0);
        boolean isIntentSafe = !activities.isEmpty();
        if(isIntentSafe){
            context.startActivity(webIntent);
        }else {
            Log.e(TAG, "没有安装浏览器");
        }
    }

    public static void checkPermission(Context context, String permission){
        int var2 = context.checkCallingOrSelfPermission(permission);
        boolean var3 = var2 == 0;
        if(!var3) {
            throw new SecurityException("Permission Denial: requires permission " + permission);
        }
    }

    public static ExecutorService newFixedThreadPool(){
        // 获取当前系统的CPU数目
        int cpuNums = Runtime.getRuntime().availableProcessors();
        //根据系统资源情况灵活定义线程池大小
        return Executors.newFixedThreadPool(cpuNums + 1);
    }
}
