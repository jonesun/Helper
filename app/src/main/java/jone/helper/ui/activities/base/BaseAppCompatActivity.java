package jone.helper.ui.activities.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

import jone.helper.R;
import jone.helper.lib.util.SystemBarTintManager;

/**
 * Created by jone.sun on 2015/9/7.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    public static final int[] themeIds = new int[]{
            R.style.HelperTheme_blue, R.style.HelperTheme_amber,
            R.style.HelperTheme_red, R.style.HelperTheme_blue_grey,
            R.style.HelperTheme_brown, R.style.HelperTheme_cyan,
            R.style.HelperTheme_deep_orange, R.style.HelperTheme_deep_purple,
            R.style.HelperTheme_green, R.style.HelperTheme_grey,
            R.style.HelperTheme_indigo, R.style.HelperTheme_orange,
            R.style.HelperTheme_teal, R.style.HelperTheme_yellow,
            R.style.HelperTheme_lime, R.style.HelperTheme_light_blue,
            R.style.HelperTheme_light_green, R.style.HelperTheme_pink,
            R.style.HelperTheme_purple
    };
    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeIndex = 0;
        try {
            themeIndex = Integer.parseInt( PreferenceManager
                    .getDefaultSharedPreferences(this).getString("theme_setting", "0"));
        }catch (Exception e){
            themeIndex = 0;
        }
        if(themeIndex < 0 || themeIndex > themeIds.length){
            themeIndex = 0;
        }
        setTheme(themeIds[themeIndex]);
        super.onCreate(savedInstanceState);
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        setContentView(getContentView());
        setStatusBarView(this, getDarkColorPrimary());
        findViews();
        initViews();
    }

    protected abstract void findViews();

    protected void initViews(){}

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public int getColorPrimary(){
        TypedValue typedValue = new  TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public int getDarkColorPrimary(){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

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

}
