package jone.helper.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureHs;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.ui.activities.base.BaseAppCompatWithLayoutActivity;

/**
 * Created by jone.sun on 2015/11/16.
 */
public class BingPicDetailActivity extends BaseAppCompatWithLayoutActivity {
    private String picUrl;
    @Override
    protected int getContentView() {
        return R.layout.activity_bing_pic_detail;
    }

    @Override
    protected void findViews() {
        if(getIntent().hasExtra("bingPicture")){
            BingPicture bingPicture = (BingPicture) getIntent().getSerializableExtra("bingPicture");
            TextView tv_title = findView(R.id.tv_title);
            ImageView iv_picture = findView(R.id.iv_picture);
            TextView tv_content = findView(R.id.tv_content);
            TextView tv_copyright = findView(R.id.tv_copyright);

            tv_title.setText(bingPicture.getMsg().get(0).getText());
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

    public static void open(Context context, BingPicture bingPicture) {
        Intent intent = new Intent(context, BingPicDetailActivity.class);
        intent.putExtra("bingPicture", bingPicture);
        context.startActivity(intent);
    }
}
