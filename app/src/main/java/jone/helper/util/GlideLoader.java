package jone.helper.util;

import android.content.Context;
import android.widget.ImageView;


import cn.lightsky.infiniteindicator.loader.ImageLoader;
import jone.helper.App;
import jone.helper.R;
public class GlideLoader implements ImageLoader {

    @Override
    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {
        targetView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (res instanceof String){
            App.getImageLoader().display(context, targetView, (String) res, R.drawable.side_nav_bar, R.drawable.side_nav_bar);
//            Glide.with(context)
//                    .load((String) res)
//                    .centerCrop()
////                .placeholder(R.drawable.a)
//                    .crossFade()
//                    .into(targetView);
        }
    }
}