package jone.helper.model.customAd;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;

import jone.helper.AppConnect;
import jone.helper.R;

/**
 *第三方广告
 * Created by jone.sun on 2015/7/2.
 */
public class Jone_AppConnectAd {
    private static final String TAG = Jone_AppConnectAd.class.getSimpleName();
    public static void showMinAd(Activity activity, LinearLayout layout_ad){
        try{
            AppConnect.getInstance(activity).setAdBackColor(activity.getResources().getColor(android.R.color.white));
            AppConnect.getInstance(activity).setAdForeColor(activity.getResources().getColor(R.color.dark_grey));
            AppConnect.getInstance(activity).showMiniAd(activity, layout_ad, 8);
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
