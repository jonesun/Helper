package jone.helper.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.camera.Camera;
import com.cooliris.media.Gallery;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import jone.helper.AppConnect;
import jone.helper.BuildConfig;
import jone.helper.Constants;
import jone.helper.R;
import jone.helper.model.Calculator.Calculator;
import jone.helper.ui.activities.base.BaseFragmentActivity;
import jone.helper.ui.fragments.*;
import jone.helper.util.UmengUtil;
import jone.helper.zxing.scan.CaptureActivity;

public class JoneHelperMainActivity extends AppCompatActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private FloatingActionButton floatingActionButton;
    private boolean isCurrentPageFirst = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setContentView(R.layout.activity_jone_helper_main);
        BaseFragmentActivity.setStatusBarView(this, getResources().getColor(R.color.jone_style_blue_700));
        fragmentManager = getSupportFragmentManager();
        initViews();
        configViews();
        initMenuFragment();
        if(savedInstanceState == null){
            changeFragment(JoneHelperMainFragment.getInstance());
        }
        MobclickAgent.updateOnlineConfig(JoneHelperMainActivity.this);
        UmengUtil.event_open_main(JoneHelperMainActivity.this);
        AppConnect.getInstance(Constants.WPSJ_ID, BuildConfig.FLAVOR, JoneHelperMainActivity.this); //万普世纪
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.update(this);
    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
    }

    private void configViews() {
        // 设置显示Toolbar
        setSupportActionBar(toolbar);
        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mActionBarDrawerToggle);

        onNavigationViewMenuItemSelected(navigationView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });
    }

    private void onNavigationViewMenuItemSelected(NavigationView mNav) {
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_item_home:
                        changeFragment(JoneHelperMainFragment.getInstance());
                        menuItem.setChecked(true);
                        break;
                    case R.id.nav_menu_item_weather:
                        changeFragment(WeatherFragment.getInstance());
                        menuItem.setChecked(true);
                        break;
                    case R.id.nav_menu_item_note:
                        startActivity(new Intent(JoneHelperMainActivity.this, NotebookActivity.class));
                        menuItem.setChecked(false);
                        break;
                    case R.id.nav_item_app_manager:
                        startActivity(new Intent(JoneHelperMainActivity.this, AppManagerActivity.class));
                        menuItem.setChecked(false);
                        break;
                    case R.id.nav_menu_item_recommend:
                        changeFragment(JoneAdListFragment.getInstance());
                        menuItem.setChecked(true);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jone_helper_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(JoneHelperMainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void changeFragment(Fragment targetFragment){
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
                    final Snackbar mSnackbar = Snackbar.make(floatingActionButton, "再按一次退出程序", Snackbar.LENGTH_LONG);
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
