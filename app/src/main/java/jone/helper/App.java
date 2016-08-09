package jone.helper;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jone.helper.greendao.dao.DaoMaster;
import jone.helper.greendao.dao.DaoSession;
import jone.helper.lib.model.imageLoader.GlideImageLoader;
import jone.helper.lib.model.imageLoader.ImageLoader;
import jone.helper.lib.model.network.NetworkOperator;
import jone.helper.lib.model.network.volley.VolleyNetworkOperator;
import jone.helper.lib.view.CommonView;
import jone.helper.ui.activities.base.ThemeTool;

/**
 * Created by jone.sun on 2016/1/12.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App instance;
    private static NetworkOperator volleyNetworkOperator;
    //    private static NetworkOperator okHttpNetworkOperator;
    private static ImageLoader imageLoader;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ThemeTool.getInstance().isThemeNight(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }
        instance = this;
        volleyNetworkOperator = VolleyNetworkOperator.getInstance().init(this);
//        okHttpNetworkOperator = OkHttpNetworkOperator.getInstance().init(this);
        CommonView.alwaysShowActionBarOverflow(getApplicationContext());//在具有硬件菜单键设备上依然显示Action bar overflow
        initAppInfo();
        imageLoader = GlideImageLoader.getInstance().init(this);
        //crash 到本地
        enabledStrictMode(); //启用严格模式
//        LeakCanary.install(this);  //内存泄露检测
        //change with develop
    }

    private void enabledStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //2.3+
                    .detectAll() //
                    .penaltyLog() //
                    .penaltyDeath() //
                    .build());
        }
    }

    private void initAppInfo() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pageInfo = pm.getPackageInfo(getPackageName(), 0);
            Log.d(TAG, "initAppInfo: versionName:" + pageInfo.versionName
                    + " VersionCode:" + pageInfo.versionCode
                    + " PackageName:" + pageInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkOperator getVolleyNetworkOperator() {
        return volleyNetworkOperator;
    }

//    public static NetworkOperator getOkHttpNetworkOperator() {
//        return okHttpNetworkOperator;
//    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

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


    public Handler getHandler() {
        return handler;
    }

    public static void showToast(String info) {
        showToast(info, Toast.LENGTH_SHORT);
    }

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static void showToast(String info, @Duration int toastTime) {
        Handler handler = App.getInstance().getHandler();
        handler.sendMessage(handler.obtainMessage(WHAT_SHOW_TOAST, toastTime, 0, info));
    }

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, Constants.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
