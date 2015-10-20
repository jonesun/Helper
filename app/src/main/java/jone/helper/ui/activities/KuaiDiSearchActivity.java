package jone.helper.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jone.helper.R;
import jone.helper.databinding.ActivityKuaiDiSearchBinding;
import jone.helper.databinding.FragmentKuaiDiSearchBinding;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.activities.base.DataBindingBaseAppCompatActivity;
import jone.helper.ui.fragments.KuaiDiSearchActivityFragment;
import jone.helper.ui.fragments.base.DataBindingBaseFragment;
import jone.helper.zxing.scan.CaptureActivity;

public class KuaiDiSearchActivity extends DataBindingBaseAppCompatActivity<ActivityKuaiDiSearchBinding> {

    @Override
    protected int getContentView() {
        return R.layout.activity_kuai_di_search;
    }

    @Override
    protected void initViews() {
        super.initViews();
        Toolbar toolbar = getViewDataBinding().toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(data.hasExtra("result")){
                String result = data.getStringExtra("result");
                Log.e("扫描返回", "result: " + result);
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if(fragment != null && fragment instanceof KuaiDiSearchActivityFragment){
                    KuaiDiSearchActivityFragment kuaiDiSearchActivityFragment = (KuaiDiSearchActivityFragment) fragment;
                    kuaiDiSearchActivityFragment.search(result);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void fbOnClick(View view) {
        CaptureActivity.openForResult(KuaiDiSearchActivity.this);
    }
}
