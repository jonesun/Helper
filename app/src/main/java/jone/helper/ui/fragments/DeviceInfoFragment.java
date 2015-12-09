package jone.helper.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.DeviceInfo;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.fragments.base.BaseFragment;

public class DeviceInfoFragment extends BaseFragment<JoneAppManagerActivity> {
    private static final String TAG = DeviceInfoFragment.class.getSimpleName();

    private Map<Integer, String> versionNameMap;
    public List<DeviceInfo> data = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private static DeviceInfoFragment instance = null;

    public static DeviceInfoFragment getInstance() {
        if (instance == null) {
            instance = new DeviceInfoFragment();
        }
        return instance;
    }

    public DeviceInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_device_info;
    }

    @Override
    protected void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
//        switch (flag) {
//            case VERTICAL_LIST:
//                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//                break;
//            case HORIZONTAL_LIST:
//                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                break;
//            case VERTICAL_GRID:
//                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.VERTICAL, false);
//                break;
//            case HORIZONTAL_GRID:
//                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.HORIZONTAL, false);
//                break;
//            case STAGGERED_GRID:
//                mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
//                break;
//        }
        //layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        initVersionMap();
        Log.e(TAG, "检测权限");
        if (ContextCompat.checkSelfPermission(getHostActivity(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                App.showToast("我们需要读取手机的状态");
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

//// Here, thisActivity is the current activity
//            if (ContextCompat.checkSelfPermission(thisActivity,
//                    Manifest.permission.READ_CONTACTS)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                // Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
//                        Manifest.permission.READ_CONTACTS)) {
//
//                    // Show an expanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//
//                } else {
//
//                    // No explanation needed, we can request the permission.
//
//                    ActivityCompat.requestPermissions(thisActivity,
//                            new String[]{Manifest.permission.READ_CONTACTS},
//                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                }
//            }
        } else {
            initData("normal");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult>>" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                //如果用户取消，permissions可能为null.
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 可以安全的调用api。
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initData("onRequestPermissionsResult");
                } else {
                    // 洗洗睡吧，没戏了。
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    App.showToast("无法获取手机状态");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initData(String from) {
        data = new ArrayList<>();
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String tel = tm.getLine1Number();
        System.out.println("imei: " + imei + ", tel: " + tel); //todo
        String deviceName = SystemUtil.isPad(getActivity()) ? "平板" : "手机";
        addItemView("本机类型", deviceName);
        addItemView("设备制造商", Build.MANUFACTURER);
        addItemView("设备品牌", Build.BRAND);
        addItemView("产品名称", Build.PRODUCT);
        addItemView("设备型号", Build.MODEL);
        String versionName = "unknown";
        if (versionNameMap != null && versionNameMap.containsKey(Build.VERSION.SDK_INT)) {
            versionName = versionNameMap.get(Build.VERSION.SDK_INT);
        }
        addItemView("系统版本", Build.VERSION.RELEASE + "(" + versionName + ")");
        addItemView("系统API级别", Build.VERSION.SDK_INT + "");

        DisplayMetrics displayMetrics = SystemUtil.getDisplayMetrics(getActivity());
        addItemView("屏幕分辨率", displayMetrics.widthPixels + " X " + displayMetrics.heightPixels + " " + SystemUtil.getScreenPhysicalSize(getActivity()));
        addItemView("屏幕密度", displayMetrics.densityDpi + "(" + displayMetrics.density + ")");

        addItemView("设备用户名", Build.USER);

        addItemView("设备主机地址", Build.HOST);
        addItemView("设备版本号", Build.ID);

        addItemView("设备驱动名称", Build.DEVICE);
        addItemView("设备显示的版本包", Build.DISPLAY);
        addItemView("设备的唯一标识", Build.FINGERPRINT);

        addItemView("设备基板名称", Build.BOARD);
        addItemView("设备引导程序版本号", Build.BOOTLOADER);

        String[] SUPPORTED_ABIS;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
        } else {
            SUPPORTED_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        if (SUPPORTED_ABIS != null && SUPPORTED_ABIS.length > 0) {
            for (int i = 1; i <= SUPPORTED_ABIS.length; i++) {
                addItemView("第" + i + "设备指令集名称(CPU的类型)", SUPPORTED_ABIS[i - 1]);
            }
        }
        addItemView("设备硬件名称", Build.HARDWARE);

        addItemView("无线电固件版本号", Build.getRadioVersion());
        addItemView("设备标签", Build.TAGS);
        addItemView("设备版本类型", Build.TYPE);

        addItemView("设备当前的系统开发代号", Build.VERSION.CODENAME);
        addItemView("系统源代码控制值", Build.VERSION.INCREMENTAL);
        Log.e(TAG, from + " >>KITKAT: " + Build.VERSION_CODES.KITKAT);
        recyclerView.setAdapter(new MyAdapter(data));
    }

    private void addItemView(String txtName, String buildInfo) {
        data.add(new DeviceInfo(txtName, buildInfo));
    }

    private void initVersionMap() {
        versionNameMap = new HashMap<>();
        versionNameMap.put(Build.VERSION_CODES.BASE, "BASE"); //October 2008: The original, first, version of Android.
        versionNameMap.put(Build.VERSION_CODES.BASE_1_1, "BASE_1_1");//February 2009: First Android update, officially called 1.1
        versionNameMap.put(Build.VERSION_CODES.CUPCAKE, "CUPCAKE"); //May 2009: Android 1.5.
        versionNameMap.put(Build.VERSION_CODES.CUR_DEVELOPMENT, "CUR_DEVELOPMENT"); //Magic version number for a current development build, which has not yet turned into an official release
        versionNameMap.put(Build.VERSION_CODES.DONUT, "DONUT"); //September 2009: Android 1.6.
        versionNameMap.put(Build.VERSION_CODES.ECLAIR, "ECLAIR"); //November 2009: Android 2.0
        versionNameMap.put(Build.VERSION_CODES.ECLAIR_0_1, "ECLAIR_0_1"); //December 2009: Android 2.0.1
        versionNameMap.put(Build.VERSION_CODES.ECLAIR_MR1, "ECLAIR_MR1"); //January 2010: Android 2.1
        versionNameMap.put(Build.VERSION_CODES.FROYO, "FROYO"); //June 2010: Android 2.2
        versionNameMap.put(Build.VERSION_CODES.GINGERBREAD, "GINGERBREAD"); //November 2010: Android 2.3
        versionNameMap.put(Build.VERSION_CODES.GINGERBREAD_MR1, "GINGERBREAD_MR1"); //February 2011: Android 2.3.3.
        versionNameMap.put(Build.VERSION_CODES.HONEYCOMB, "HONEYCOMB"); //February 2011: Android 3.0.
        versionNameMap.put(Build.VERSION_CODES.HONEYCOMB_MR1, "HONEYCOMB_MR1"); //May 2011: Android 3.1.
        versionNameMap.put(Build.VERSION_CODES.HONEYCOMB_MR2, "HONEYCOMB_MR2"); //June 2011: Android 3.2.
        versionNameMap.put(Build.VERSION_CODES.ICE_CREAM_SANDWICH, "ICE_CREAM_SANDWICH"); //October 2011: Android 4.0.
        versionNameMap.put(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1, "ICE_CREAM_SANDWICH_MR1"); //December 2011: Android 4.0.3.
        versionNameMap.put(Build.VERSION_CODES.JELLY_BEAN, "JELLY_BEAN"); //June 2012: Android 4.1.
        versionNameMap.put(Build.VERSION_CODES.JELLY_BEAN_MR1, "JELLY_BEAN_MR1"); //Android 4.2: Moar jelly beans!
        versionNameMap.put(Build.VERSION_CODES.JELLY_BEAN_MR2, "JELLY_BEAN_MR2"); //Android 4.3: Jelly Bean MR2, the revenge of the beans.
        versionNameMap.put(Build.VERSION_CODES.KITKAT, "KITKAT"); //Android 4.4: KitKat, another tasty treat.
        versionNameMap.put(Build.VERSION_CODES.KITKAT_WATCH, "KITKAT_WATCH"); //Android 4.4W: KitKat for watches, snacks on the run
        versionNameMap.put(Build.VERSION_CODES.LOLLIPOP, "LOLLIPOP"); //Lollipop.  A flat one with beautiful shadows.  But still tasty.
        versionNameMap.put(Build.VERSION_CODES.LOLLIPOP_MR1, "LOLLIPOP_MR1");//Lollipop with an extra sugar coating on the outside!
        versionNameMap.put(Build.VERSION_CODES.M, "Marshmallow"); //M comes after L.
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<DeviceInfo> data;

        public MyAdapter(List<DeviceInfo> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder holder = new ViewHolder(LayoutInflater
                    .from(viewGroup.getContext()).inflate(R.layout.item_device_info_layout, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.text.setText(data.get(i).getName() + ":  " + data.get(i).getValue());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
