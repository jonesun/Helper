package jone.helper.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import jone.helper.R;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.activities.base.BaseAppCompatWithLayoutActivity;
import jone.helper.ui.fragments.KuaiDiSearchActivityFragment;
import jone.helper.zxing.scan.CaptureActivity;

public class KuaiDiSearchActivity extends BaseAppCompatActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_kuai_di_search;
    }

    @Override
    protected void findViews() {
        Toolbar toolbar = findView(R.id.toolbar);
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
