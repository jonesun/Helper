package jone.helper.lib.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2014/9/18.
 */
public class SystemUtil {
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

    /**
     * s判断是否是平板
     * @param context
     * @return
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断设备是否带有闪光灯
     * @param context
     * @return
     */
    public static boolean isHaveFlashlight(Context context){
        boolean hasFlashLight = false;
        FeatureInfo[] feature= context.getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo featureInfo : feature) {
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) {
                hasFlashLight=true;
                break;
            }
        }
        return hasFlashLight;
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 精确获取屏幕尺寸（例如：3.5、4.0、5.0寸屏幕）
     * @param ctx
     * @return
     */
    public static double getScreenPhysicalSize(Activity ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        return diagonalPixels / (160 * dm.density);
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
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    private static void checkNetworkConnection(Context context) {

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected) {
//                Log.i(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
//                Log.i(TAG, getString(R.string.mobile_connection));
            }
        } else {
//            Log.i(TAG, getString(R.string.no_wifi_or_mobile));
        }

    }

    public static boolean hasNetWork(Context context) {
        checkNetworkConnection(context);
        if(wifiConnected || mobileConnected){
            return true;
        }
        return false;
    }

    public static void uninstallAPK(Context context, String packageName){
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent=new Intent(Intent.ACTION_DELETE,uri);
        context.startActivity(intent);
    }

    /***
     * 在小米应用图标的快捷方式上加数字<br>
     *
     *
     * @param context
     * @param num 显示的数字：大于99，为"99"，当为""时，不显示数字，相当于隐藏了)<br><br>
     *
     * 注意点：
     * context.getPackageName()+"/."+clazz.getSimpleName() （这个是启动activity的路径）中的"/."不能缺少
     *
     */
    public static void xiaoMiShortCut(Context context,Class<?> clazz, String num){
        Log.e("SystemUtil", "xiaoMiShortCut....");
        Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
        localIntent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName()+"/."+clazz.getSimpleName());
        if(TextUtils.isEmpty(num)){
            num = "";
        }else{
            int numInt = Integer.valueOf(num);
            if (numInt > 0){
                if (numInt > 99){
                    num = "99";
                }
            }else{
                num = "0";
            }
        }
        localIntent.putExtra("android.intent.extra.update_application_message_text", num);
        context.sendBroadcast(localIntent);
    }


    public static void sendBadgeNum(Context context, String number){
        String launcherActivityClassName = getLaunchActivityName(context);
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendBadgeNumToXiaoMi(context, launcherActivityClassName, number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendBadgeNumToSamsumg(context, launcherActivityClassName, number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendBadgeNumToSony(context, launcherActivityClassName, number);
        } else {
            Toast.makeText(context, "Not Support", Toast.LENGTH_LONG).show();
        }
    }
    public static void sendBadgeNumToXiaoMi(Context context,String launcherActivityClassName, String number) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有"+number+"未读消息");
            builder.setTicker("您有"+number+"未读消息");
            builder.setAutoCancel(true);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            //field.set(miuiNotification, number);// 设置信息数
            field.set(miuiNotification, Integer.valueOf(number));// 红米2，改一行代码就可以设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
            Toast.makeText(context, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Log.e("SystemUtil", "sendBageNumToXiaoMi", e);
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/"+ launcherActivityClassName);
            localIntent.putExtra("android.intent.extra.update_application_message_text",number);
            context.sendBroadcast(localIntent);
        }finally{
            if(notification!=null && isMiUIV6 ){
                //miui6以上版本需要使用通知发送
                nm.notify(0, notification);
            }
        }
    }

    public static void sendBadgeNumToSony(Context context,String launcherActivityClassName, String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",launcherActivityClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",context.getPackageName());//包名
        context.sendBroadcast(localIntent);

        Toast.makeText(context, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    public static void sendBadgeNumToSamsumg(Context context,String launcherActivityClassName, String number){
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);//数字
        localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name", launcherActivityClassName ); //启动页
        context.sendBroadcast(localIntent);
        Toast.makeText(context, "Samsumg," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    /***
     * 取得当前应用的启动activity的名称：
     * mainfest.xml中配置的 android:name:"
     * @param context
     * @return
     */
    public static String getLaunchActivityName(Context context){
        PackageManager localPackageManager = context.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        try{
            for (ResolveInfo localResolveInfo : localPackageManager.queryIntentActivities(localIntent, 0)) {
                if (!localResolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(context.getPackageName()))
                    continue;
                return localResolveInfo.activityInfo.name;
            }
        } catch (Exception localException){
            return null;
        }
        return null;
    }
}
