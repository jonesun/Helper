package jone.helper.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import jone.helper.R;
import jone.helper.lib.model.imageCache.ImageCacheManager;
import jone.helper.lib.volley.VolleyCommon;
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
//        ImageLoader.ImageListener listener = ImageLoader.getImageListener(photo_view,
//                R.mipmap.bg02, R.mipmap.bg02);
        ImageCacheManager.getInstance().getImageLoader().get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    progress_bar.setVisibility(View.GONE);
                    photo_view.setImageBitmap(response.getBitmap());
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

            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    public static void open(Context context, String imageUrl){
        Intent intent = new Intent(context, ZoomImageViewActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);
    }
}
