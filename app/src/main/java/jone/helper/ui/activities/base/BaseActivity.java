package jone.helper.ui.activities.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by jone.sun on 2015/8/24.
 */
public abstract class BaseActivity extends Activity {
    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        findViews();
        initViews();
    }

    protected abstract void findViews();

    protected void initViews(){}

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
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
