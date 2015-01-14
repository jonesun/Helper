package jone.helper;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

import jone.helper.lib.VolleyCommon;
import jone.helper.lib.view.CommonView;
import jone.helper.util.JoneORMLiteHelper;
import jone.helper.util.NDKFileUtil;

/**
 * Created by Administrator on 2014/9/23.
 */
public class App extends Application {
    private static App instance;
    private Handler handler;
    private VolleyCommon volleyCommon;
    private static JoneORMLiteHelper joneORMLiteHelper;

    public static App getInstance() {
        return instance;
    }

    public VolleyCommon getVolleyCommon() {
        return volleyCommon;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("app start...");
        instance = this;
        handler = new Handler();
        volleyCommon = new VolleyCommon(getApplicationContext());
        CommonView.alwaysShowActionBarOverflow(getApplicationContext());//在具有硬件菜单键设备上依然显示Action bar overflow
    }

    public Handler getHandler() {
        return handler;
    }
}
