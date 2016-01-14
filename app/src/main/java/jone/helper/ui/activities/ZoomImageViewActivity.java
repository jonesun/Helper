package jone.helper.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.model.imageLoader.ImageLoaderListener;
import jone.helper.ui.widget.zoom.Info;
import jone.helper.ui.widget.zoom.PhotoView;

/**
 * Created by jone.sun on 2015/11/10.
 */
public class ZoomImageViewActivity extends Activity {
    private PhotoView photo_view;
    private ProgressBar progress_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        if(TextUtils.isEmpty(imageUrl)){
            finish();
            return;
        }
        setContentView(R.layout.activity_zoom_image);
        photo_view = (PhotoView) findViewById(R.id.photo_view);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        App.getImageLoader().getBitmap(ZoomImageViewActivity.this, imageUrl,
                new ImageLoaderListener() {
                    @Override
                    public void onDone(Bitmap bitmap) {
                        progress_bar.setVisibility(View.GONE);
                        if (bitmap != null) {
                            photo_view.setImageBitmap(bitmap);
                            photo_view.enable();
                            Info info = photo_view.getInfo();
                            photo_view.animaFrom(info);
                            photo_view.animaTo(info, new Runnable() {
                                @Override
                                public void run() {
                                    //动画完成监听
                                }
                            });
                        }
                    }
                });
    }

    public static void open(Context context, String imageUrl){
        Intent intent = new Intent(context, ZoomImageViewActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);
    }
}
