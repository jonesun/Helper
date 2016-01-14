package jone.helper.ui.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.umeng.analytics.MobclickAgent;

import jone.helper.R;

/**
 * Created by jone.sun on 2015/11/16.
 */
public abstract class BaseAppCompatWithLayoutActivity extends AppCompatActivity {
    private ThemeTool themeTool;
    private Toolbar toolbar;
    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeTool = ThemeTool.getInstance();
        themeTool.setPageTheme(this, savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_app_compat_with_layout);
        themeTool.setStatusBarView(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FrameLayout layout_center = findView(R.id.layout_center);
        layout_center.addView(getLayoutInflater().inflate(getContentView(), null));
        findViews();
        initViews();
    }

    public void setTitle(String title){
        if(toolbar != null){
            toolbar.setTitle(title);
        }
    }

    protected abstract void findViews();

    protected void initViews(){}

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
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
