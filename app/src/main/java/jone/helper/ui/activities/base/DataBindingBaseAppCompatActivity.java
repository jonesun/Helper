package jone.helper.ui.activities.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import jone.helper.R;

/**
 * Created by jone.sun on 2015/10/19.
 */
public abstract class DataBindingBaseAppCompatActivity<V extends ViewDataBinding> extends AppCompatActivity {
    private V viewDataBinding;
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
        viewDataBinding = DataBindingUtil
                .setContentView(this, getContentView());

        themeTool.setStatusBarView(this, getDarkColorPrimary());
        initViews();
    }

    protected void initViews(){}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        themeTool.onSaveInstanceState(outState);
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

    public V getViewDataBinding() {
        return viewDataBinding;
    }
}
