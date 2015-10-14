package jone.helper.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.camera.Camera;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cooliris.media.Gallery;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import jone.helper.App;
import jone.helper.BitmapCache;
import jone.helper.R;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.Utils;
import jone.helper.lib.volley.VolleyCommon;
import jone.helper.model.Calculator.Calculator;
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureMsg;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.model.bing.OnBingPictureListener;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.fragments.JoneHelperMainFragment;
import jone.helper.ui.fragments.WeatherFragment;
import jone.helper.ui.setting.SettingsActivity;
import jone.helper.util.SharedToUtil;
import jone.helper.util.UmengUtil;
import jone.helper.zxing.scan.CaptureActivity;

public class HelperMainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMenuItemClickListener, OnMenuItemLongClickListener {
    private final String TAG = HelperMainActivity.class.getSimpleName();
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private FloatingActionButton fab;

    private NetworkImageView iv_picture;
    private TextView tv_title, tv_copyright;

    private boolean isCurrentPageFirst;

    @Override
    protected int getContentView() {
        startActivity(new Intent(this, SplashActivity.class));
        return R.layout.activity_helper_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            changeFragment(JoneHelperMainFragment.getInstance());
        }
    }

    @Override
    protected void findViews() {
        fragmentManager = getSupportFragmentManager();
        initViews();


        MobclickAgent.updateOnlineConfig(HelperMainActivity.this);
        UmengUtil.event_open_main(HelperMainActivity.this);
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.update(this);
    }

    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavHeaderView(navigationView);
        initMenuFragment();
    }

    private void initNavHeaderView(NavigationView navigationView){
        iv_picture = (NetworkImageView) navigationView.findViewById(R.id.iv_picture);
        tv_title = (TextView) navigationView.findViewById(R.id.tv_title);
        tv_copyright = (TextView) navigationView.findViewById(R.id.tv_copyright);
        iv_picture.setDefaultImageResId(R.drawable.side_nav_bar);
        iv_picture.setErrorImageResId(R.drawable.side_nav_bar);
        BingPictureOperator.getInstance().getDailyPictureUrl(new OnBingPictureListener() {
            @Override
            public void onSuccess(BingPicture bingPicture) {
                Log.e(TAG, "bingPicture: " + GsonUtils.toJson(bingPicture));
                if (bingPicture != null) {
                    iv_picture.setImageUrl(bingPicture.getUrl(), new ImageLoader(VolleyCommon.getInstance(App.getInstance()).getmRequestQueue(),
                            new BitmapCache()));
                    List<BingPictureMsg> bingPictureMsgs = bingPicture.getMsg();
                    if (bingPictureMsgs != null && bingPictureMsgs.size() > 0) {
                        tv_title.setText(bingPictureMsgs.get(0).getText());
                    }
                    tv_copyright.setText(bingPicture.getCopyright());
                }
            }

            @Override
            public void onError(String reason) {
                Log.e(TAG, "reason: " + reason);
            }
        });

    }

    public void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_helper_main, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        if(targetFragment instanceof JoneHelperMainFragment){
            isCurrentPageFirst = true;
        }else {
            isCurrentPageFirst = false;
        }
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            exit();
        }
    }

    private void exit(){
        if(isCurrentPageFirst){
            if((System.currentTimeMillis() - exitTime) > 2000){
                final Snackbar mSnackbar = Snackbar.make(fab, "再按一次退出程序", Snackbar.LENGTH_LONG);
                mSnackbar.show();
                mSnackbar.setAction("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSnackbar.dismiss();
                        finish();
                        System.exit(0);
                    }
                });
                exitTime = System.currentTimeMillis();
            }
            else{
                finish();
                System.exit(0);
            }
        }else {
            changeFragment(JoneHelperMainFragment.getInstance());
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.helper_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(HelperMainActivity.this, SettingsActivity.class));
            return true;
        }else if (id == R.id.action_about) {
            startActivity(new Intent(HelperMainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_menu_item_home:
                changeFragment(JoneHelperMainFragment.getInstance());
                break;
            case R.id.nav_menu_item_weather:
                changeFragment(WeatherFragment.getInstance());
                break;
            case R.id.nav_menu_item_note:
                startActivity(new Intent(HelperMainActivity.this, NotebookActivity.class));
                break;
            case R.id.nav_item_app_manager:
                startActivity(new Intent(HelperMainActivity.this, JoneAppManagerActivity.class));
                break;
            case R.id.nav_menu_item_logistics:
//                        changeFragment(JoneAdListFragment.getInstance());
//                        menuItem.setChecked(true);

                startActivity(new Intent(HelperMainActivity.this, KuaiDiSearchActivity.class));
                break;
            case R.id.nav_menu_item_share:
                try {
                    SharedToUtil.shareToWeixin(HelperMainActivity.this,
                            "欢迎来到帮手的世界\nhttp://shouji.baidu.com/software/item?docid=7577214&from=as",
                            Utils.getSharedPicFile(HelperMainActivity.this));
                } catch (Exception e) {
                    Toast.makeText(HelperMainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_menu_item_feedback:
                FeedbackAgent fb = new FeedbackAgent(HelperMainActivity.this);
                fb.startFeedbackActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject menu_camera = new MenuObject(getString(R.string.camera));
        menu_camera.setResource(android.R.drawable.ic_menu_camera);

        MenuObject menu_gallery = new MenuObject(getString(R.string.photos));
        menu_gallery.setResource(android.R.drawable.ic_menu_gallery);

        MenuObject menu_calculator = new MenuObject(getString(R.string.calculator));
        menu_calculator.setResource(R.mipmap.ic_menu_emoticons);

        MenuObject menu_scan = new MenuObject(getString(R.string.scan));
        menu_scan.setResource(R.mipmap.ic_menu_find);

        MenuObject menu_flashlight = new MenuObject(getString(R.string.flashlight));
        menu_flashlight.setResource(R.mipmap.ic_menu_paste);

        menuObjects.add(menu_camera);
        menuObjects.add(menu_gallery);
        menuObjects.add(menu_calculator);
        menuObjects.add(menu_scan);
        menuObjects.add(menu_flashlight);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:
                UmengUtil.event_click_camera(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, Camera.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 1:
                UmengUtil.event_click_photos(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, Gallery.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 2:
                UmengUtil.event_click_calculator(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, Calculator.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 3:
                UmengUtil.event_click_scan(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, CaptureActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 4:
                UmengUtil.event_click_scan(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, FlashlightActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        //Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

}
