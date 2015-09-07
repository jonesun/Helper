package jone.helper.ui.activities.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import jone.helper.lib.util.SystemBarTintManager;

/**
 * Created by jone.sun on 2015/7/2.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        findViews();
        initViews();
        setOnClick();
        setStatusBarView(this, getResources().getColor(android.R.color.holo_blue_bright));
    }

    protected abstract void findViews();

    protected void initViews(){}

    protected void setOnClick(){}

    /**
     * 设置状态栏的颜色，目前只是在4.4上面有效
     */
    public static void setStatusBarView(Activity activity, int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);
            tintManager.setTintColor(color);
        }
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }
}
