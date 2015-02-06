package jone.helper.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.android.camera.Camera;
import com.cooliris.media.Gallery;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.app.Calculator.Calculator;
import jone.helper.bean.ToolBean;
import jone.helper.flashlight.FlashlightActivity;
import jone.helper.lib.util.SystemUtil;
import jone.helper.zxing.scan.CaptureActivity;

/**
 * Created by Administrator on 2014/9/18.
 */
public class ToolsLogic {
    private Activity activity;
    public ToolsLogic(Activity activity){
        this.activity = activity;
    }
    public List<ToolBean> getToolBeans(){
        List<ToolBean> toolBeans = new ArrayList<ToolBean>();
        if(!SystemUtil.isPad(activity)){
            toolBeans.add(new ToolBean(1, activity.getString(R.string.call_phone), R.drawable.ic_menu_call, null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.startActivity(new Intent(Intent.ACTION_DIAL));
                        }
                    }));
            toolBeans.add(new ToolBean(2, activity.getString(R.string.send_message), R.drawable.ic_menu_send_sms, null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")));
                        }
                    }));
        }
        toolBeans.add(new ToolBean(3, activity.getString(R.string.camera), R.drawable.ic_menu_camera, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, Camera.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));
        toolBeans.add(new ToolBean(4, activity.getString(R.string.photos), R.drawable.ic_menu_gallery, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, Gallery.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));
        toolBeans.add(new ToolBean(5, activity.getString(R.string.calculator), R.drawable.ic_menu_default, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, Calculator.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));
        toolBeans.add(new ToolBean(7, activity.getString(R.string.scan), R.drawable.ic_menu_scan, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, CaptureActivity.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));

        toolBeans.add(new ToolBean(9, activity.getString(R.string.flashlight), R.drawable.ic_menu_flashlight, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, FlashlightActivity.class));
                        activity.overridePendingTransition(R.anim.zoomin, R.anim.zoomout); //类似iphone的进入和退出时的效果
                    }
                }));
        return toolBeans;
    }
}
