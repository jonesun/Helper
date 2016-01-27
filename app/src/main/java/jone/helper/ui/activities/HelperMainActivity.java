package jone.helper.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.lightsky.infiniteindicator.InfiniteIndicatorLayout;
import cn.lightsky.infiniteindicator.slideview.BaseSliderView;
import cn.lightsky.infiniteindicator.slideview.DefaultSliderView;
import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.util.Utils;
import jone.helper.model.Calculator.Calculator;
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureMsg;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.model.bing.OnBingPictureListener;
import jone.helper.services.MessengerService;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.dialog.ChooseThemeDialogFragment;
import jone.helper.ui.fragments.HelperMainFragment;
import jone.helper.ui.setting.SettingsActivity;
import jone.helper.util.SharedToUtil;
import jone.helper.util.UmengUtil;
import jone.helper.zxing.scan.BuildConfig;
import jone.helper.zxing.scan.CaptureActivity;

public class HelperMainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMenuItemClickListener, OnMenuItemLongClickListener {
    private final String TAG = HelperMainActivity.class.getSimpleName();
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private FloatingActionButton fab;

    private InfiniteIndicatorLayout infinite_indicator_layout;
    private ImageView iv_picture;
    private TextView tv_title, tv_copyright;

    private boolean isCurrentPageFirst;

    private LocalBroadcastManager localBroadcastManager;
    private Messenger messenger, reply;
    public static final int WHAT_RESPONSE_PICTURE_LIST = 20001;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "回调成功" + msg.what);
            switch (msg.what){
                case WHAT_RESPONSE_PICTURE_LIST:
                    Bundle bundle = msg.getData();
                    bundle.setClassLoader(getClassLoader());
                    if(bundle.containsKey("bingPictureList")){
                        ArrayList<BingPicture> bingPictureList = bundle.getParcelableArrayList("bingPictureList");
                        initIndicator(bingPictureList);
                    }
                    break;
            }
        }
    };
    @Override
    protected int getContentView() {
        return R.layout.activity_helper_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.startMethodTracing("Entertainment");//生成日志，运行TraceView
        if(savedInstanceState == null){
            if(PreferenceManager
                    .getDefaultSharedPreferences(HelperMainActivity.this)
                    .getBoolean("first_open_" + BuildConfig.VERSION_NAME, true)){
                startActivity(new Intent(HelperMainActivity.this, GuideActivity.class));
                PreferenceManager
                        .getDefaultSharedPreferences(HelperMainActivity.this)
                        .edit()
                        .putBoolean("first_open_" + BuildConfig.VERSION_NAME, false)
                        .apply();
            }else if(Utils.isNetworkAlive(HelperMainActivity.this)){
                startActivity(new Intent(this, SplashActivity.class));
            }
            changeFragment(HelperMainFragment.getInstance());
        } else {
            if(savedInstanceState.containsKey("isCurrentPageFirst")){
                isCurrentPageFirst = savedInstanceState.getBoolean("isCurrentPageFirst");
            }
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(HelperMainActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("jone.helper.activity.recreate");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        reply = new Messenger(handler);
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), MessengerService.class.getCanonicalName());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            App.showToast("bind success");
            Log.e(TAG, name.toString() + "onServiceConnected");
            messenger = new Messenger(service);
            sendMessage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, name.toString() + "onServiceDisconnected");
        }
    };

    public void sendMessage(){
        Message message = Message.obtain(null, MessengerService.WHAT_REQUEST_PICTURE_LIST);
        Bundle bundle = new Bundle();
        bundle.putString("name", "我是" + TAG);
        message.setData(bundle);
        // 设置回调用的Messenger
        message.replyTo = reply;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action)){
                if(action.equals("jone.helper.activity.recreate")){
                    getThemeTool().refreshTheme(HelperMainActivity.this);
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isCurrentPageFirst", isCurrentPageFirst);
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.app_name));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white)); //设置收缩后Toolbar上字体的颜色
        collapsingToolbar.setExpandedTitleColor(getThemeTool().getColorPrimary(HelperMainActivity.this)); //设置还没收缩时状态下字体颜色
        collapsingToolbar.setExpandedTitleGravity(Gravity.RIGHT);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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

    private void initNavHeaderView(final NavigationView navigationView){
        final SwitchCompat switchCompat = (SwitchCompat) navigationView.getMenu()
                .findItem(R.id.night_model).getActionView();
        switchCompat.setChecked(getThemeTool().isThemeNight(HelperMainActivity.this));
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getThemeTool().setThemeNight(HelperMainActivity.this, switchCompat.isChecked());
            }
        });
        //解决23.1.0的bug
        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                navigationView.removeOnLayoutChangeListener(this);
                iv_picture = (ImageView) navigationView.findViewById(R.id.iv_picture);
                tv_title = (TextView) navigationView.findViewById(R.id.tv_title);
                tv_copyright = (TextView) navigationView.findViewById(R.id.tv_copyright);
//        iv_picture.setDefaultImageResId(R.drawable.side_nav_bar);
//        iv_picture.setErrorImageResId(R.drawable.side_nav_bar);
                BingPictureOperator.getInstance().getDailyPictureUrl(new OnBingPictureListener() {
                    @Override
                    public void onSuccess(final BingPicture bingPicture) {
//                        Log.e(TAG, "bingPicture: " + GsonUtils.toJson(bingPicture));
                        if (bingPicture != null && iv_picture != null) {
                            App.getImageLoader().display(HelperMainActivity.this,
                                    iv_picture, bingPicture.getUrl(),
                                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);
                            iv_picture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BingPicDetailActivity.open(HelperMainActivity.this, bingPicture);
                                }
                            });
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
        });

    }

    private void initIndicator(ArrayList<BingPicture> bingPictureList){
        infinite_indicator_layout = findView(R.id.infinite_indicator_layout);
        if(bingPictureList != null && bingPictureList.size() > 0){
            for (final BingPicture bingPicture : bingPictureList) {
                DefaultSliderView textSliderView = new DefaultSliderView(HelperMainActivity.this);
                textSliderView
                        .image(bingPicture.getUrl())
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .showImageResForEmpty(R.drawable.side_nav_bar)
                        .showImageResForError(R.drawable.side_nav_bar)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                Bundle bundle = slider.getBundle();
                                if (bundle.containsKey("bingPicture")) {
                                    BingPicDetailActivity.open(HelperMainActivity.this, (BingPicture) bundle.getSerializable("bingPicture"));
                                } else {
                                    ZoomImageViewActivity.open(HelperMainActivity.this,
                                            BingPictureOperator.getFullImageUrl(HelperMainActivity.this, slider.getUrl()));
                                }
                            }
                        });
                textSliderView.getBundle()
                        .putString("extra", bingPicture.getUrl());
                textSliderView.getBundle().putParcelable("bingPicture", bingPicture);
                infinite_indicator_layout.addSlider(textSliderView);
            }
            infinite_indicator_layout.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Center_Bottom);
            infinite_indicator_layout.startAutoScroll();
        }
    }

    public void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_helper_main, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        if(targetFragment instanceof HelperMainFragment){
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
            changeFragment(HelperMainFragment.getInstance());
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
//            case R.id.nav_menu_item_home:
//                changeFragment(HelperMainFragment.getInstance());
//                break;
            case R.id.nav_menu_item_picture:
                startActivity(new Intent(HelperMainActivity.this, PicturesTabActivity.class));
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
            case R.id.nav_menu_item_theme:
//                onTheme(new Random().nextInt(10));
                ChooseThemeDialogFragment chooseThemeDialogFragment = new ChooseThemeDialogFragment(HelperMainActivity.this);
                chooseThemeDialogFragment.show(getSupportFragmentManager(), "ChooseThemeDialogFragment");
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

        MenuObject menu_gallery = new MenuObject(getString(R.string.photos));
        menu_gallery.setResource(android.R.drawable.ic_menu_gallery);

        MenuObject menu_calculator = new MenuObject(getString(R.string.calculator));
        menu_calculator.setResource(R.mipmap.ic_menu_emoticons);

        MenuObject menu_scan = new MenuObject(getString(R.string.scan));
        menu_scan.setResource(R.mipmap.ic_menu_find);

        MenuObject menu_flashlight = new MenuObject(getString(R.string.flashlight));
        menu_flashlight.setResource(R.mipmap.ic_menu_paste);

        menuObjects.add(menu_gallery);
        menuObjects.add(menu_calculator);
        menuObjects.add(menu_scan);
        menuObjects.add(menu_flashlight);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
//        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:
                UmengUtil.event_click_photos(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, Gallery.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 1:
                UmengUtil.event_click_calculator(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, Calculator.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 2:
                UmengUtil.event_click_scan(HelperMainActivity.this);
                startActivity(new Intent(HelperMainActivity.this, CaptureActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case 3:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(localBroadcastManager != null && broadcastReceiver != null){
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
        }
        unbindService(serviceConnection);
//        Debug.stopMethodTracing();
    }
}
