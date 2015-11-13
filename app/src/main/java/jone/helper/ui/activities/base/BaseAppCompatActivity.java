package jone.helper.ui.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by jone.sun on 2015/9/7.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private ThemeTool themeTool;
    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeTool = ThemeTool.getInstance();
        themeTool.setPageTheme(this, savedInstanceState);
        super.onCreate(savedInstanceState);
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        setContentView(getContentView());
        themeTool.setStatusBarView(this);
        findViews();
        initViews();
    }

    protected abstract void findViews();

    protected void initViews(){}

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public void onTheme(int themeIndex){
        if(themeTool != null){
            themeTool.onTheme(this, themeIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        themeTool.onSaveInstanceState(outState);
    }

    public ThemeTool getThemeTool() {
        return themeTool;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
