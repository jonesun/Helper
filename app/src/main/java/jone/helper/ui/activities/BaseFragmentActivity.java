package jone.helper.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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
    }

    protected abstract void findViews();

    protected abstract void initViews();

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }
}
