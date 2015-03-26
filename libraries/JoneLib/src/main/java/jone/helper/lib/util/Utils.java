package jone.helper.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();
            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
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
}
