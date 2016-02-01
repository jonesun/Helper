package jone.helper.ui.activities;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.PictureBean;
import jone.helper.lib.model.imageLoader.ImageLoaderListener;
import jone.helper.lib.util.BitmapUtil;
import jone.helper.ui.widget.EnhancedMenuInflater;
import jone.helper.ui.widget.photoview.HackyViewPager;
import jone.helper.ui.widget.photoview.PhotoView;

/**
 * 图片列表展示
 * Created by jone.sun on 2016/1/22.
 */
public class PictureViewPagerActivity extends AppCompatActivity {
    public static final String KEY_PICTURE_LIST = "picture_list";
    public static final String KEY_PICTURE_INDICATOR = "picture_indicator";
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TextView text_title, text_indicator;
    private List<PictureBean> pictureBeanList;
    private ColorDrawable colorDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view_pager);
        findViews();
    }

    protected void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        colorDrawable = new ColorDrawable(Color.parseColor("#33000000"));
        if(getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            getSupportActionBar().setSplitBackgroundDrawable(colorDrawable);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.rv_bottom).setBackgroundColor(Color.parseColor("#50000000"));

        text_title = (TextView) findViewById(R.id.text_title);
        text_indicator = (TextView) findViewById(R.id.text_indicator);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        pictureBeanList = getIntent().getParcelableArrayListExtra(KEY_PICTURE_LIST);
        if(pictureBeanList == null || pictureBeanList.size() == 0){
            throw new RuntimeException("pictureBeanList size is null!");
        }
        int picture_indicator = getIntent().getIntExtra(KEY_PICTURE_INDICATOR, 0) + 1;
        text_title.setText(pictureBeanList.get(picture_indicator - 1).getTitle());
        text_indicator.setText(getString(R.string.picture_indicator,
                picture_indicator, pictureBeanList.size()));
        mViewPager.setAdapter(new SamplePagerAdapter(PictureViewPagerActivity.this, pictureBeanList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                text_title.setText(pictureBeanList.get(position).getTitle());
                text_indicator.setText(getString(R.string.picture_indicator,
                        position + 1, pictureBeanList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(picture_indicator - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_picture_view_pager_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mViewPager != null && pictureBeanList != null && pictureBeanList.size() > 0){
            final PictureBean currentPictureBean = pictureBeanList.get(mViewPager.getCurrentItem());
            switch (item.getItemId()) {
                case R.id.item_save:
                    App.getImageLoader().getBitmap(this,
                            currentPictureBean.getUri(), new ImageLoaderListener() {
                                @Override
                                public void onDone(Bitmap bitmap) {
                                    String fileName = currentPictureBean.getTitle() + (mViewPager.getCurrentItem() + 1) + ".jpg";
                                    File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                    if(dir == null){
                                        dir = Environment.getExternalStorageDirectory();
                                    }
                                    if(BitmapUtil.saveBitmapToSD(dir, fileName, bitmap)){
                                        App.showToast("图片已成功保存到: " + dir.getPath() + File.separator + fileName);
                                    }else {
                                        App.showToast("图片保存失败");
                                    }
                                }
                            });
                    break;
                case R.id.item_set_wallpaper:
                    App.getImageLoader().getBitmap(this,
                            currentPictureBean.getUri(), new ImageLoaderListener() {
                                @Override
                                public void onDone(Bitmap bitmap) {
                                    WallpaperManager mWallManager= WallpaperManager.getInstance(PictureViewPagerActivity.this);
                                    try {
                                        mWallManager.setBitmap(bitmap);
                                        App.showToast("成功设置" + currentPictureBean.getTitle() + "为壁纸");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        App.showToast("设置壁纸失败");
                                    }
                                }
                            });
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    static class SamplePagerAdapter extends PagerAdapter {
        private Context context;
        List<PictureBean> pictureBeanList;

        public SamplePagerAdapter(Context context, List<PictureBean> pictureBeanList){
            this.context = context;
            this.pictureBeanList = pictureBeanList;
        }

        @Override
        public int getCount() {
            return pictureBeanList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            App.getImageLoader().display(context, photoView,
                    pictureBeanList.get(position).getUri(),
                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public static void open(Context context, ArrayList<PictureBean> pictureBeanList){
        Intent intent = new Intent(context, PictureViewPagerActivity.class);
        intent.putParcelableArrayListExtra(KEY_PICTURE_LIST, pictureBeanList);
        context.startActivity(intent);
    }

    public static void open(Context context, ArrayList<PictureBean> pictureBeanList, int picture_indicator){
        Intent intent = new Intent(context, PictureViewPagerActivity.class);
        intent.putParcelableArrayListExtra(KEY_PICTURE_LIST, pictureBeanList);
        intent.putExtra(KEY_PICTURE_INDICATOR, picture_indicator);
        context.startActivity(intent);
    }
}
