package jone.helper.ui.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.Camera;
import com.cooliris.media.Gallery;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import jone.helper.App;
import jone.helper.AppConnect;
import jone.helper.BuildConfig;
import jone.helper.Constants;
import jone.helper.R;
import jone.helper.app.Calculator.Calculator;
import jone.helper.lib.util.Utils;
import jone.helper.ui.fragments.*;
import jone.helper.ui.view.ResideMenu;
import jone.helper.ui.view.ResideMenuItem;
import jone.helper.util.SharedToUtil;
import jone.helper.util.UmengUtil;
import jone.helper.zxing.scan.CaptureActivity;

public class MenuActivity extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemShared;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemFeedback;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setScreenOrientation(this);
        setContentView(R.layout.menu_main);
        mContext = this;
        setUpMenu();
        if( savedInstanceState == null ){
            changeFragment(WeatherFragment.getInstance());
        }
        setFloatingActionButton();
        MobclickAgent.updateOnlineConfig(MenuActivity.this);
        UmengUtil.event_open_main(MenuActivity.this);
        AppConnect.getInstance(Constants.WPSJ_ID, BuildConfig.FLAVOR, MenuActivity.this); //万普世纪
    }

    private void setFloatingActionButton(){
        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_gallery));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_default));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_scan));

        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        rLSubBuilder.setLayoutParams(blueParams);
        rLSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
                UmengUtil.event_click_floating_action_menu(MenuActivity.this);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_camera(MenuActivity.this);
                startActivity(new Intent(MenuActivity.this, Camera.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_photos(MenuActivity.this);
                startActivity(new Intent(MenuActivity.this, Gallery.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_calculator(MenuActivity.this);
                startActivity(new Intent(MenuActivity.this, Calculator.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_scan(MenuActivity.this);
                startActivity(new Intent(MenuActivity.this, CaptureActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.bg02);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,     getString(R.string.title_section0));
        itemProfile  = new ResideMenuItem(this, R.drawable.icon_profile,  getString(R.string.title_section1));
        itemShared = new ResideMenuItem(this, R.drawable.icon_share, getString(R.string.share));
        itemCalendar = new ResideMenuItem(this, R.drawable.icon_calendar, getString(R.string.title_section2));
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, getString(R.string.title_section3));
        itemFeedback = new ResideMenuItem(this, R.drawable.icon_profile, getString(R.string.title_section4));

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemShared.setOnClickListener(this);
        itemCalendar.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);

//        if(!BuildConfig.FLAVOR.equals("baidu")){
//            resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
//        }
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemShared, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemFeedback, ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (view == itemHome){
            changeFragment(WeatherFragment.getInstance());
        }else if (view == itemProfile){
            try{
                AppConnect.getInstance(MenuActivity.this).showGameOffers(MenuActivity.this);
            }catch (Exception e){
                Toast.makeText(MenuActivity.this, "服务器异常,请稍候再试", Toast.LENGTH_SHORT).show();
            }
        }else if(view == itemShared){
            try{
                SharedToUtil.shareToWeixin(MenuActivity.this, "欢迎来到帮手的世界\nhttp://shouji.baidu.com/software/item?docid=7577214&from=as", Utils.getSharedPicFile(MenuActivity.this));
            }catch (Exception e){
                Toast.makeText(MenuActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
            }
        }else if (view == itemCalendar){
            changeFragment(AllAppsFragment.getInstance());
        }else if (view == itemSettings){
            changeFragment(DeviceInfoFragment.getInstance());
        }else if(view == itemFeedback){
            try{
                //用户反馈
                AppConnect.getInstance(MenuActivity.this).showFeedback(MenuActivity.this);
            }catch (Exception e){}
        }
        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    public void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(MenuActivity.this).close();
    }
}
