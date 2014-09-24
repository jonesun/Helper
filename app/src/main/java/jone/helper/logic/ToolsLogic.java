package jone.helper.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

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
            toolBeans.add(new ToolBean(1, "拨打电话", R.drawable.ic_menu_call, null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.startActivity(new Intent(Intent.ACTION_DIAL));
                        }
                    }));
            toolBeans.add(new ToolBean(2, "发送信息", R.drawable.ic_menu_send_sms, null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")));
                        }
                    }));
        }
        toolBeans.add(new ToolBean(3, "计算器", R.drawable.ic_menu_default, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, Calculator.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));
        toolBeans.add(new ToolBean(7, "条码扫描", R.drawable.ic_menu_scan, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, CaptureActivity.class));
                        activity.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);//由左向右滑入的效果
                    }
                }));

        toolBeans.add(new ToolBean(9, "手电筒", R.drawable.ic_menu_flashlight, null,
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
