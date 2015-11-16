package jone.helper;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;

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
    private static NetOperator<Map<String, String>, String> netStringOperator;
    private static NetOperator<JSONObject, JSONObject> netJsonOperator;
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        netStringOperator = NetStringOperator.getInstance(this);
        netJsonOperator = NetJSONObjectOperator.getInstance(this);
        CommonView.alwaysShowActionBarOverflow(getApplicationContext());//在具有硬件菜单键设备上依然显示Action bar overflow
        initAppInfo();
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

    public static void showToast(String info, int toastTime) {
        Handler handler = App.getInstance().getHandler();
        handler.sendMessage(handler.obtainMessage(WHAT_SHOW_TOAST, toastTime, 0, info));
    }
}
