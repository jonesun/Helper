package jone.helper.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

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

    public void event_click_call_phone(){
        MobclickAgent.onEvent(context, "event_click_call_phone");
    }

    public void event_click_send_message(){
        MobclickAgent.onEvent(context, "event_click_send_message");
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

    public void event_click_bluetooth_chat(){
        MobclickAgent.onEvent(context, "event_click_bluetooth_chat");
    }
}
