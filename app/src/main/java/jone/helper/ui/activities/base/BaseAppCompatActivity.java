package jone.helper.ui.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jone.sun on 2015/9/7.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
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
}
