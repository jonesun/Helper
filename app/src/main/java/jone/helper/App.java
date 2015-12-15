package jone.helper;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import jone.helper.lib.model.imageCache.ImageCacheManager;
import jone.helper.lib.model.net.NetJSONObjectOperator;
import jone.helper.lib.model.net.NetOperator;
import jone.helper.lib.model.net.NetStringOperator;
import jone.helper.lib.view.CommonView;

/**
 * Created by Administrator on 2014/9/23.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App instance;
    private static final int WHAT_SHOW_TOAST = 1010101;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SHOW_TOAST:
                    if (msg.obj != null) {
                        String info = msg.obj.toString();
                        if (msg.arg1 == Toast.LENGTH_SHORT) {
                            Toast.makeText(instance, info, Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(instance, info, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    break;
            }
        }

    };
    private static int DISK_IMAGE_CACHE_SIZE = 1024*1024*10;
    private static Bitmap.CompressFormat DISK_IMAGE_CACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGE_CACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
    private static NetOperator<Map<String, String>, String> netStringOperator;
    private static NetOperator<JSONObject, JSONObject> netJsonOperator;
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createImageCache();
        netStringOperator = NetStringOperator.getInstance(this);
        netJsonOperator = NetJSONObjectOperator.getInstance(this);
        CommonView.alwaysShowActionBarOverflow(getApplicationContext());//在具有硬件菜单键设备上依然显示Action bar overflow
        initAppInfo();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //Log.e(TAG, "onActivityCreated: " + activity.getComponentName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "onActivityStarted: " + activity.getComponentName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "onActivityResumed: " + activity.getComponentName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "onActivityPaused: " + activity.getComponentName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "onActivityStopped: " + activity.getComponentName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d(TAG, "onActivitySaveInstanceState: " + activity.getComponentName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "onActivityDestroyed: " + activity.getComponentName());
            }
        });
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache(){
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGE_CACHE_SIZE
                , DISK_IMAGE_CACHE_COMPRESS_FORMAT
                , DISK_IMAGE_CACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
    }

    private void initAppInfo(){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pageInfo=pm.getPackageInfo(getPackageName(),0);
            Log.d(TAG, "initAppInfo: versionName:" + pageInfo.versionName
                    + " VersionCode:" + pageInfo.versionCode
                    + " PackageName:" + pageInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetOperator<Map<String, String>, String> getNetStringOperator() {
        return netStringOperator;
    }

    public static NetOperator<JSONObject, JSONObject> getNetJsonOperator() {
        return netJsonOperator;
    }

    public Handler getHandler() {
        return handler;
    }

    public static void showToast(String info) {
        showToast(info, Toast.LENGTH_SHORT);
    }

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}
    public static void showToast(String info, @Duration int toastTime) {
        Handler handler = App.getInstance().getHandler();
        handler.sendMessage(handler.obtainMessage(WHAT_SHOW_TOAST, toastTime, 0, info));
    }
}
