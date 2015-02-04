package jone.helper.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2014/12/24.
 */
public class Utils {
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
}
