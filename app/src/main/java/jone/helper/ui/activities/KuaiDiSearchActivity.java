package jone.helper.ui.activities;

import android.support.v7.widget.Toolbar;
import android.view.View;

import jone.helper.R;
import jone.helper.ui.activities.base.BaseAppCompatActivity;

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
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
