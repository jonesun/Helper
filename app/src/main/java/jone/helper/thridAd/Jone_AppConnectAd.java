package jone.helper.thridAd;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;

import jone.helper.AppConnect;

/**
 *第三方广告
 * Created by jone.sun on 2015/7/2.
 */
public class Jone_AppConnectAd {
    private static final String TAG = Jone_AppConnectAd.class.getSimpleName();
    public static void showMinAd(Activity activity, LinearLayout layout_ad){
        try{
            AppConnect.getInstance(activity).setAdBackColor(Color.WHITE);
            AppConnect.getInstance(activity).setAdForeColor(Color.BLACK);
            AppConnect.getInstance(activity).showMiniAd(activity, layout_ad, 8);
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
