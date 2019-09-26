package jone.helper.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import jone.helper.R;
import jone.helper.ui.activities.base.BaseAppCompatActivity;

/**
 * Created by jone.sun on 2015/11/13.
 */
public class TestActivity extends BaseAppCompatActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_test;
    }

    @Override
    protected void findViews() {
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
//                R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(getString(R.string.app_name));
//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(new AppsRecyclerViewAdapter(this));
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg02);
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(final Palette palette) {
//                int defaultColor = getResources().getColor(R.color.blue_100);
//                int defaultTitleColor = getResources().getColor(R.color.white);
//                int bgColor = palette.getDarkVibrantColor(defaultColor);
//                int titleColor = palette.getLightVibrantColor(defaultTitleColor);
//                collapsingToolbar.setContentScrimColor(bgColor);
//                collapsingToolbar.setCollapsedTitleTextColor(titleColor);
//                collapsingToolbar.setExpandedTitleColor(titleColor);
//            }
//        });
    }
}
