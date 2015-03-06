package jone.helper.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jone.sun on 2015/2/11.
 */
public class UmengUtil {

    public static void event_open_main(Context context){
        MobclickAgent.onEvent(context, "event_open_main");
    }

    public static void get_location(Context context, String url, String city){
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("city", city);
        MobclickAgent.onEvent(context, "get_location", data);
    }

    public static void get_weather(Context context, String url, String weather){
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("weather", weather);
        MobclickAgent.onEvent(context, "get_weather", data);
    }

    public static void event_click_camera(Context context){
        MobclickAgent.onEvent(context, "event_click_camera");
    }

    public static void event_click_photos(Context context){
        MobclickAgent.onEvent(context, "event_click_photos");
    }

    public static void event_click_calculator(Context context){
        MobclickAgent.onEvent(context, "event_click_calculator");
    }

    public static void event_click_scan(Context context){
        MobclickAgent.onEvent(context, "event_click_scan");
    }

    public static void event_click_flashlight(Context context){
        MobclickAgent.onEvent(context, "event_click_flashlight");
    }

    public static void event_click_floating_action_menu(Context context){
        MobclickAgent.onEvent(context, "event_click_floating_action_menu");
    }
}
