package jone.helper.thridAd;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.baidu.appx.BDBannerAd;

/**
 * Created by jone.sun on 2015/7/21.
 */
public class JoneBaiduAd {

    private static final String TAG = JoneBaiduAd.class.getSimpleName();
    public static BDBannerAd showBDBannerAd(Activity activity, ViewGroup layoutAd){
        Log.e(TAG, "执行百度广告");
        BDBannerAd bannerAdView = new BDBannerAd(activity, "24gWbsngPWaFuunqWzgr588T",
                "Hu4bqsNad8quxCFqCRSvsLRu");

        // 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
//        bannerAdView.setAdSize(BDBannerAd.SIZE_320X50);

        // 设置横幅广告行为监听器
        bannerAdView.setAdListener(new BDBannerAd.BannerAdListener() {

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Log.e(TAG, "load failure");
            }

            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                Log.e(TAG, "load success");
            }

            @Override
            public void onAdvertisementViewDidClick() {
                Log.e(TAG, "on click");
            }

            @Override
            public void onAdvertisementViewDidShow() {
                Log.e(TAG, "on show");
            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {
                Log.e(TAG, "leave app");
            }
        });
        layoutAd.addView(bannerAdView);
        layoutAd.invalidate();
        return bannerAdView;
    }
}
