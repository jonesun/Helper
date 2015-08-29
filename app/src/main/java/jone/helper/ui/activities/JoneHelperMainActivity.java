package jone.helper.ui.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.Camera;
import com.cooliris.media.Gallery;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.michaelevans.colorart.library.ColorArt;

import java.util.ArrayList;
import java.util.List;

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

public class JoneHelperMainActivity extends FragmentActivity implements View.OnClickListener, OnMenuItemClickListener,
        OnMenuItemLongClickListener {

    private ResideMenu resideMenu;
    private String[] resideMenuItemNames = new String[]{
            "menu_item_home", "menu_item_weather", "menu_item_recommend",
            "menu_item_app_manager", "menu_item_feedback", "menu_item_shared"
    };
    private boolean isCurrentPageFirst = true;
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private ImageView image_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setScreenOrientation(this);
        setContentView(R.layout.menu_main);
        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        setUpMenu();
        if( savedInstanceState == null ){
            changeFragment(JoneHelperMainFragment.getInstance());
        }
        MobclickAgent.updateOnlineConfig(JoneHelperMainActivity.this);
        UmengUtil.event_open_main(JoneHelperMainActivity.this);
        AppConnect.getInstance(Constants.WPSJ_ID, BuildConfig.FLAVOR, JoneHelperMainActivity.this); //万普世纪

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg01);
        ColorArt colorArt = new ColorArt(bitmap);
        BaseFragmentActivity.setStatusBarView(this, colorArt.getBackgroundColor());
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        image_menu = (ImageView) findViewById(R.id.image_menu);
        image_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject menu_camera = new MenuObject(getString(R.string.camera));
        menu_camera.setResource(R.drawable.ic_menu_camera);

        MenuObject menu_gallery = new MenuObject(getString(R.string.photos));
        menu_gallery.setResource(R.drawable.ic_menu_gallery);

        MenuObject menu_calculator = new MenuObject(getString(R.string.calculator));
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_default);
        menu_calculator.setBitmap(b);

        MenuObject menu_scan = new MenuObject(getString(R.string.scan));
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_scan));
        menu_scan.setDrawable(bd);

        MenuObject menu_flashlight = new MenuObject(getString(R.string.flashlight));
        menu_flashlight.setResource(R.drawable.ic_menu_default);

        menuObjects.add(menu_camera);
        menuObjects.add(menu_gallery);
        menuObjects.add(menu_calculator);
        menuObjects.add(menu_scan);
        menuObjects.add(menu_flashlight);
        return menuObjects;
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
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
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
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:
                UmengUtil.event_click_camera(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Camera.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 1:
                UmengUtil.event_click_photos(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Gallery.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 2:
                UmengUtil.event_click_calculator(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, Calculator.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 3:
                UmengUtil.event_click_scan(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, CaptureActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 4:
                UmengUtil.event_click_scan(JoneHelperMainActivity.this);
                startActivity(new Intent(JoneHelperMainActivity.this, FlashlightActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        //Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(JoneHelperMainActivity.this).close();
    }
}
