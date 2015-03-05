package jone.helper.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jone.sun on 2015/2/11.
 */
public class UmengUtil {
    private Context context;
    public static UmengUtil install;
    public static UmengUtil getInstall(Context context){
        if(install == null){
            install = new UmengUtil(context);
        }
        return install;
    }

    private UmengUtil(Context context){
        this.context = context;
    }

    public void event_open_main(){
        MobclickAgent.onEvent(context, "event_open_main");
    }

    public void get_location(String url, String response, String city){
        Map<String, String> data = new HashMap<String, String>();
        data.put("url", url);
        data.put("response", response);
        data.put("city", city);
        MobclickAgent.onEvent(context, "get_location", data);
    }

    public void get_weather(String url, String response, String weather){
        Map<String, String> data = new HashMap<String, String>();
        data.put("url", url);
        data.put("response", response);
        data.put("weather", weather);
        MobclickAgent.onEvent(context, "get_weather", data);
    }

    public void event_click_camera(){
        MobclickAgent.onEvent(context, "event_click_camera");
    }

    public void event_click_photos(){
        MobclickAgent.onEvent(context, "event_click_photos");
    }

    public void event_click_calculator(){
        MobclickAgent.onEvent(context, "event_click_calculator");
    }

    public void event_click_scan(){
        MobclickAgent.onEvent(context, "event_click_scan");
    }

    public void event_click_flashlight(){
        MobclickAgent.onEvent(context, "event_click_flashlight");
    }

    public void event_click_floating_action_menu(){
        MobclickAgent.onEvent(context, "event_click_floating_action_menu");
    }
}
