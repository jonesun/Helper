package jone.helper.ui.activities;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureHs;
import jone.helper.model.bing.BingPictureMsg;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.ui.activities.base.BaseAppCompatWithLayoutActivity;

/**
 * Created by jone.sun on 2015/11/16.
 */
public class BingPicDetailActivity extends BaseAppCompatWithLayoutActivity {
    private String picUrl;
    private TextView tv_title;
    @Override
    protected int getContentView() {
        return R.layout.activity_bing_pic_detail;
    }

    @Override
    protected void findViews() {
        if(getIntent().hasExtra("bingPicture")){
            BingPicture bingPicture = getIntent().getParcelableExtra("bingPicture");
            tv_title = findView(R.id.tv_title);
            ImageView iv_picture = findView(R.id.iv_picture);
            TextView tv_content = findView(R.id.tv_content);
            TextView tv_copyright = findView(R.id.tv_copyright);

            ArrayList<BingPictureMsg> bingPictureMsgList = bingPicture.getMsg();
            if(bingPictureMsgList != null
                    && bingPictureMsgList.size() > 0){
                BingPictureMsg bingPictureMsg = bingPictureMsgList.get(0);
                if(!TextUtils.isEmpty(bingPictureMsg.getText())){
                    tv_title.setText(bingPicture.getMsg().get(0).getText());
                }
            }
            picUrl = BingPictureOperator.getFullImageUrl(BingPicDetailActivity.this, bingPicture.getUrl());
            App.getImageLoader().display(BingPicDetailActivity.this,
                    iv_picture, picUrl,
                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);

            iv_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZoomImageViewActivity.open(BingPicDetailActivity.this,
                            picUrl);
                }
            });

            List<BingPictureHs> bingPictureHsList = bingPicture.getHs();
            if(bingPictureHsList != null && bingPictureHsList.size() > 0){
                StringBuilder stringBuffer = new StringBuilder();
                for(BingPictureHs bingPictureHs : bingPictureHsList){
                    stringBuffer.append("\r\n").append(bingPictureHs.getDesc())
                            .append(bingPictureHs.getQuery())
                            .append("\r\n");
                }
                tv_content.setText(stringBuffer.toString());
            }
            tv_copyright.setText(String.format(getResources().getString(R.string.bing_copyright),
                    bingPicture.getCopyright()));
        }else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_picture_view_pager_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                App.getImageLoader().getBitmap(this,
                        picUrl, new ImageLoaderListener() {
                            @Override
                            public void onDone(Bitmap bitmap) {
                                String fileName = tv_title.getText() + ".jpg";
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
                        picUrl, new ImageLoaderListener() {
                            @Override
                            public void onDone(Bitmap bitmap) {
                                WallpaperManager mWallManager= WallpaperManager.getInstance(BingPicDetailActivity.this);
                                try {
                                    mWallManager.setBitmap(bitmap);
                                    App.showToast("成功设置" + tv_title.getText() + "为壁纸");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    App.showToast("设置壁纸失败");
                                }
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void open(Context context, BingPicture bingPicture) {
        Intent intent = new Intent(context, BingPicDetailActivity.class);
        intent.putExtra("bingPicture", bingPicture);
        context.startActivity(intent);
    }
}
