package jone.helper.ui.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
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

import jone.helper.AppConnect;
import jone.helper.BuildConfig;
import jone.helper.Constants;
import jone.helper.R;
import jone.helper.model.Calculator.Calculator;
import jone.helper.lib.util.Utils;
import jone.helper.ui.fragments.*;
import jone.helper.ui.view.ResideMenu;
import jone.helper.ui.view.ResideMenuItem;
import jone.helper.util.ResUtil;
import jone.helper.util.SharedToUtil;
import jone.helper.util.UmengUtil;
import jone.helper.zxing.scan.CaptureActivity;

public class JoneHelperMainActivity extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private String[] resideMenuItemNames = new String[]{
            "menu_item_home", "menu_item_weather", "menu_item_recommend",
            "menu_item_app_manager", "menu_item_feedback", "menu_item_shared"
    };
    private boolean isCurrentPageFirst = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setScreenOrientation(this);
        setContentView(R.layout.menu_main);
        setUpMenu();
        if( savedInstanceState == null ){
            changeFragment(JoneHelperMainFragment.getInstance());
        }
        setFloatingActionButton();
        MobclickAgent.updateOnlineConfig(JoneHelperMainActivity.this);
        UmengUtil.event_open_main(JoneHelperMainActivity.this);
        AppConnect.getInstance(Constants.WPSJ_ID, BuildConfig.FLAVOR, JoneHelperMainActivity.this); //万普世纪
    }

    private void setFloatingActionButton(){
        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageResource(R.mipmap.ic_action_new_light);
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageResource(R.drawable.ic_menu_camera);
        rlIcon2.setImageResource(R.drawable.ic_menu_gallery);
        rlIcon3.setImageResource(R.drawable.ic_menu_default);
        rlIcon4.setImageResource(R.drawable.ic_menu_scan);

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
                UmengUtil.event_click_floating_action_menu(JoneHelperMainActivity.this);
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
                UmengUtil.event_click_camera(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Camera.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_photos(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Gallery.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_calculator(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Calculator.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.event_click_scan(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, CaptureActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.mipmap.bg02);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        int index = resideMenuItemNames.length / 2;
        for(int i = 0; i < resideMenuItemNames.length; i++){
            int direction;
            if(i < index){
                direction = ResideMenu.DIRECTION_LEFT;
            }else {
                direction = ResideMenu.DIRECTION_RIGHT;
            }
            getResideMenuItem(resideMenuItemNames[i], direction);
        }

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

    private ResideMenuItem getResideMenuItem(String name, int direction){
        ResideMenuItem menuItem = new ResideMenuItem(this, ResUtil.getMipmapResId(name), ResUtil.getStringResId(name));
        menuItem.setTag(name);
        menuItem.setOnClickListener(this);
        resideMenu.addMenuItem(menuItem, direction);
        return menuItem;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if(tag != null && tag instanceof String){
            String name = tag.toString();
            switch (name){
                case "menu_item_home":
                    changeFragment(JoneHelperMainFragment.getInstance());
                    break;
                case "menu_item_weather":
                    changeFragment(WeatherFragment.getInstance());
                    break;
                case "menu_item_recommend":
                    changeFragment(JoneAdListFragment.getInstance());
                    break;
                case "menu_item_app_manager":
                    startActivity(new Intent(JoneHelperMainActivity.this, AppManagerActivity.class));
                    break;
                case "menu_item_feedback":
                    try{
                        AppConnect.getInstance(JoneHelperMainActivity.this).showFeedback(JoneHelperMainActivity.this);
                    }catch (Exception e){}
                    break;
                case "menu_item_shared":
                    try{
                        SharedToUtil.shareToWeixin(JoneHelperMainActivity.this, "欢迎来到帮手的世界\nhttp://shouji.baidu.com/software/item?docid=7577214&from=as", Utils.getSharedPicFile(JoneHelperMainActivity.this));
                    }catch (Exception e){
                        Toast.makeText(JoneHelperMainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
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
        if(targetFragment instanceof JoneHelperMainFragment){
            isCurrentPageFirst = true;
        }else {
            isCurrentPageFirst = false;
        }
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
            if(isCurrentPageFirst){
                if((System.currentTimeMillis() - exitTime) > 2000){
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                }
                else{
                    finish();
                    System.exit(0);
                }
            }else {
                changeFragment(JoneHelperMainFragment.getInstance());
                resideMenu.closeMenu();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(JoneHelperMainActivity.this).close();
    }
}
