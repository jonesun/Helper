package jone.helper.flashlight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import jone.helper.lib.util.SystemUtil;
import jone.helper.lib.view.ColorPickerDialog;
import jone.helper.R;

public class FlashlightActivity extends Activity {
    private static final String TAG = FlashlightActivity.class.getSimpleName();
    private ViewGroup layoutMain;
    private CheckBox checkBoxFlashLight;

    boolean hasFlashLight=false;
    Camera camera;

    @SuppressLint("AppCompatMethod")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setScreenOrientation(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置背景常亮
        setContentView(R.layout.activity_flashlight);
        checkBoxFlashLight = (CheckBox) findViewById(R.id.checkBoxFlashLight);
        hasFlashLight = SystemUtil.isHaveFlashlight(FlashlightActivity.this);
        if(hasFlashLight){
            controlHardwareFlashlight();
        }else {
            controlScreenFlashlight();
        }
    }

    /**
     * 控制闪光灯
     */
    private void controlHardwareFlashlight(){
        checkBoxFlashLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview(); // 开始亮灯
                }else {
                    camera.stopPreview(); // 关掉亮灯
                    camera.release(); // 关掉照相机
                }
            }
        });
        checkBoxFlashLight.setChecked(true);
    }

    /**
     * 控制屏幕亮度
     */
    private void controlScreenFlashlight(){
        Toast.makeText(FlashlightActivity.this, "不支持闪光灯,改用屏幕照明。", Toast.LENGTH_LONG).show();
        checkBoxFlashLight.setVisibility(View.GONE);
        layoutMain = (ViewGroup) findViewById(R.id.layoutMain);
        layoutMain.setBackgroundColor(getResources().getColor(android.R.color.white));
        //设置背景亮度为最高
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1.0f;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){ //只有在按下的时候才响应事件
            if(!hasFlashLight) {
                new ColorPickerDialog(FlashlightActivity.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        layoutMain.setBackgroundColor(color);
                    }
                }, android.R.color.white).show();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(hasFlashLight && camera != null){
            camera.release(); // 关掉照相机
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
