package jone.helper.ui.activities.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import jone.helper.R;
import jone.helper.lib.util.SystemBarTintManager;

/**
 * Created by jone.sun on 2015/10/19.
 */
public class ThemeTool {
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
    private int themeIndex = 0;

    public static ThemeTool instance = null;

    public static ThemeTool getInstance(){
        if(instance == null){
            instance = new ThemeTool();
        }
        return instance;
    }

    public void setPageTheme(Activity activity, Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("themeIndex")) {
                themeIndex = savedInstanceState.getInt("themeIndex");
            }
        }else {
            try {
                themeIndex = Integer.parseInt(PreferenceManager
                        .getDefaultSharedPreferences(activity).getString("theme_setting", "0"));
            }catch (Exception e){
                themeIndex = 0;
            }
        }
        if(themeIndex < 0 || themeIndex > themeIds.length){
            themeIndex = 0;
        }
        activity.setTheme(themeIds[themeIndex]);  //设置主题皮肤
    }

    public void onTheme(Activity activity, int themeIndex){
        this.themeIndex = themeIndex;
        PreferenceManager
                .getDefaultSharedPreferences(activity).edit()
                .putString("theme_setting", themeIndex + "").apply();
        activity.recreate();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("themeIndex", themeIndex);
    }

    /**
     * 设置状态栏的颜色，目前只是在4.4上面有效
     */
    public void setStatusBarView(Activity activity, int color) {
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
