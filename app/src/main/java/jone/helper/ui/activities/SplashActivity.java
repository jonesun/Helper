package jone.helper.ui.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;
import java.util.concurrent.ExecutionException;

import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.util.SystemUtil;
import jone.helper.lib.util.Utils;
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureMsg;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.model.bing.OnBingPictureListener;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getSimpleName();
    private ImageView iv_picture;
    private TextView tv_title, tv_copyright;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(Utils.isNetworkAlive(SplashActivity.this)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closePage();
                }
            }, 5000);
            iv_picture = (ImageView) findViewById(R.id.iv_picture);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_copyright = (TextView) findViewById(R.id.tv_copyright);
            BingPictureOperator.getInstance().getDailyPictureUrl(new OnBingPictureListener() {
                @Override
                public void onSuccess(final BingPicture bingPicture) {
                    try{
                        if (bingPicture != null) {
                            App.getImageLoader().display(SplashActivity.this,
                                    iv_picture, BingPictureOperator.getFullImageUrl(SplashActivity.this, bingPicture.getUrl()),
                                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);
                            iv_picture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BingPicDetailActivity.open(SplashActivity.this, bingPicture);
                                    closePage();
                                }
                            });
                            List<BingPictureMsg> bingPictureMsgs = bingPicture.getMsg();
                            if (bingPictureMsgs != null && bingPictureMsgs.size() > 0) {
                                tv_title.setText(bingPictureMsgs.get(0).getText());
                            }
                            tv_copyright.setText(bingPicture.getCopyright());
                        }else {
                            closePage();
                        }
                    }catch (Exception e){

                    }

                }

                @Override
                public void onError(String reason) {
                    Log.e(TAG, "reason: " + reason);
                    closePage();
                }
            });
        } else {
            closePage();
        }
    }

    private void closePage(){
        finish();
        overridePendingTransition(R.anim.enter_alpha, R.anim.exit_alpha);
    }
}
