package jone.helper.model.bing;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jone.helper.App;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.lib.util.SystemUtil;

/**http://www.bing.com/HPImageArchive.aspx?format=js&idx=1&n=1&mkt=en-US
 * format：接口返回格式，已知可选项xml,js
 * idx:日期表示0为当天，-1为明天，1为昨天，2为后天，依次类推，已知可选项-1 ~ 18
 * n: 个数
 * mkt:地区编号(可选项)，不同地区会获得不同壁纸。已知可选项en-US, zh-CN, ja-JP, en-AU, de-DE, en-NZ, en-CA
 * Created by jone.sun on 2015/9/21.
 */
public class BingPictureOperator {
    private static BingPictureOperator instance = null;
    public static BingPictureOperator getInstance(){
        if(instance == null){
            instance = new BingPictureOperator();
        }
        return instance;
    }
    public void getDailyPictureUrl(final OnBingPictureListener onBingPictureListener){
        StringBuilder url = new StringBuilder("http://www.bing.com/HPImageArchive.aspx");
        url.append("?").append("format=").append("js")
                .append("&").append("idx=").append(new Random().nextInt(10) + "")
                .append("&").append("n=1");
//                .append("&").append("mkt=").append("zh-CN");
        NetworkRequest request = new NetworkRequest.Builder()
                .get()
                .url(url.toString())
                .setUsingCacheWithNoNetwork(true).build();
        App.getVolleyNetworkOperator().request(request, JSONObject.class, new ResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response, boolean fromCache) {
//                Log.e("getDailyPictureUrl", "response: " + response.toString());
                BingPicture bingPicture = null;
                try {
                    if (response.has("images")) {
                        JSONArray results = response.getJSONArray("images");
                        if (results != null && results.length() > 0) {
                            String data = results.get(0).toString();
                            bingPicture = new Gson().fromJson(data, BingPicture.class);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("getDailyPictureUrl", "onSuccess", e);
                } finally {
                    if(onBingPictureListener != null){
                        onBingPictureListener.onSuccess(bingPicture);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("getDailyPictureUrl", "获取失败，请检查网络连接: " + e.getMessage());
                if(onBingPictureListener != null){
                    onBingPictureListener.onError(e.getMessage());
                }
            }
        });
    }

    public void getPictureUrls(int count, final OnBingPicturesListener onBingPicturesListener){
        StringBuilder url = new StringBuilder("http://www.bing.com/HPImageArchive.aspx");
        url.append("?").append("format=").append("js")
                .append("&").append("n=").append(count);
        NetworkRequest request = new NetworkRequest.Builder()
                .get()
                .url(url.toString())
                .setUsingCacheWithNoNetwork(true).build();
        App.getVolleyNetworkOperator().request(request, JSONObject.class, new ResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response, boolean fromCache) {
//                Log.e("getPictureUrls", fromCache + ">>response: " + response.toString());
                ArrayList<BingPicture> bingPictureList = new ArrayList<>();
                try {
                    if (response.has("images")) {
                        JSONArray results = response.getJSONArray("images");
                        if (results != null && results.length() > 0) {
                            for(int i = 0; i < results.length(); i++){
                                String data = results.get(i).toString();
                                bingPictureList.add(new Gson().fromJson(data, BingPicture.class));
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e("getDailyPictureUrl", "onSuccess", e);
                } finally {
                    if(onBingPicturesListener != null){
                        onBingPicturesListener.onSuccess(bingPictureList);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("getDailyPictureUrl", "获取失败，请检查网络连接: " + e.getMessage());
                if(onBingPicturesListener != null){
                    onBingPicturesListener.onError(e.getMessage());
                }
            }
        });
    }

    public static String getFullImageUrl(Activity activity, String imageUrl){
        String url = imageUrl;
        DisplayMetrics displayMetrics = SystemUtil.getDisplayMetrics(activity);
        String suffix = url.substring(url.lastIndexOf("."));
        try{
            url = url.substring(0, url.lastIndexOf("_") + 1);
            //http://s.cn.bing.net/az/hprichbg/rb/IxtapaJellyfish_ZH-CN9411866711_480x800.jpg
            url = url + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels + suffix;
        }catch (Exception e){
            url = imageUrl;
        }
        return url;
    }

//    {
//        "images": [
//        {
//            "startdate": "20150920",
//                "fullstartdate": "201509201600",
//                "enddate": "20150921",
//                "url": "http://s.cn.bing.net/az/hprichbg/rb/IxtapaJellyfish_ZH-CN9411866711_1920x1080.jpg",
//                "urlbase": "/az/hprichbg/rb/IxtapaJellyfish_ZH-CN9411866711",
//                "copyright": "墨西哥，格雷罗州，伊斯塔帕，紫水母 (© Christian Vizl/Tandem Stills + Motion)",
//                "copyrightlink": "http://www.bing.com/search?q=%E7%B4%AB%E6%B0%B4%E6%AF%8D&form=hpcapt&mkt=zh-cn",
//                "wp": true,
//                "hsh": "98a787aec7b3bbaa0d6dc5f26edf6c76",
//                "drk": 1,
//                "top": 1,
//                "bot": 1,
//                "hs": [
//            {
//                "desc": "有时候羡慕海里自由飘荡的水母，没心没肺。",
//                    "link": "http://www.bing.com/search?q=%E6%B0%B4%E6%AF%8D+%E6%B2%A1%E6%9C%89%E5%BF%83%E8%84%8F&form=hphot1&mkt=zh-cn",
//                    "query": "因为，水母真的没有。",
//                    "locx": 12,
//                    "locy": 44
//            },
//            {
//                "desc": "像水母一样，",
//                    "link": "http://www.bing.com/images/search?q=%E6%B0%B4%E6%AF%8D&form=hphot2&mkt=zh-cn",
//                    "query": "飘。",
//                    "locx": 43,
//                    "locy": 47
//            },
//            {
//                "desc": "飘过广阔海洋，",
//                    "link": "http://www.bing.com/images/search?q=%E4%BC%8A%E6%96%AF%E5%A1%94%E5%B8%95&FORM=hphot3&mkt=zh-cn",
//                    "query": "穿过伊斯塔帕的碧海蓝天。",
//                    "locx": 57,
//                    "locy": 33
//            }
//            ],
//            "msg": [
//            {
//                "title": "今日图片故事",
//                    "link": "http://www.bing.com/search?q=%E7%B4%AB%E6%B0%B4%E6%AF%8D&form=pgbar1&mkt=zh-cn",
//                    "text": "紫水母"
//            }
//            ]
//        }
//        ],
//        "tooltips": {
//        "loading": "正在加载...",
//                "previous": "上一页",
//                "next": "下一页",
//                "walle": "此图片不能下载用作壁纸。",
//                "walls": "下载今日美图。仅限用作桌面壁纸。"
//    }
//    }
//    在线工具 由 开源中国 所有 | @
}
