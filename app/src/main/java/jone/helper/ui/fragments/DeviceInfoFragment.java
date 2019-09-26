package jone.helper.ui.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        String deviceName = SystemUtil.isPad(getActivity()) ? "平板" : "手机";
        addItemView("设备信息", Build.BRAND + " " + Build.PRODUCT + " " + deviceName + " " + getNumCores() + "核");
        String versionName = "unknown";
        if (versionNameMap != null && versionNameMap.containsKey(Build.VERSION.SDK_INT)) {
            versionName = versionNameMap.get(Build.VERSION.SDK_INT);
        }
        addItemView("系统版本", "android" + Build.VERSION.RELEASE + "(" + versionName + ") " + Build.VERSION.SDK_INT);

        DisplayMetrics displayMetrics = SystemUtil.getDisplayMetrics(getActivity());
        addItemView("屏幕分辨率", displayMetrics.widthPixels + " X " + displayMetrics.heightPixels + " " + SystemUtil.getScreenPhysicalSize(getActivity()));
        addItemView("屏幕密度", displayMetrics.densityDpi + "(" + displayMetrics.density + ")");

        addItemView("内存大小", getAvailMemory() + "/" + getTotalMemory());
        TelephonyManager tm = (TelephonyManager) getContext().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (!TextUtils.isEmpty(imei)) {
            addItemView("IMEI", imei);
        }
        String imsi = tm.getSubscriberId();
        if (!TextUtils.isEmpty(imsi)) {
            addItemView("IMSI", imsi);
        }
        String serviceName = tm.getSimOperatorName(); // 运营商
        if (!TextUtils.isEmpty(serviceName)) {
            addItemView("运营商", serviceName);
        }
        String line1Number = tm.getLine1Number(); // 手机号码
        if (!TextUtils.isEmpty(line1Number)) {
            addItemView("手机号码", line1Number);
        }

        addItemView("设备制造商", Build.MANUFACTURER);
        addItemView("设备型号", Build.MODEL);

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
//        Log.e(TAG, from + " >>KITKAT: " + Build.VERSION_CODES.KITKAT);

        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) getContext().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor item : sensors) {
            String sensorType = getSensorType(item.getType());
            if (!TextUtils.isEmpty(sensorType)) {
                addItemView(sensorType, item.getName());
            } else {
                addItemView("传感器名称", item.getName());
            }
//            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
//            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
//            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
//            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
//            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
//            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
//            strLog.append(" Power - " + item.getPower() + "\r\n");
//            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
//            strLog.append("\r\n");
//            iIndex++;
        }

//        得到鍵盤信息,使用的語言，手機的網絡代碼（mnc）,手機的國家代碼(mcc),手機的模式，手機的方向，觸摸屏的判斷等，通過以下語句獲取：      Configuration config = getResources().getConfiguration();
        recyclerView.setAdapter(new MyAdapter(data));
    }

    private void addItemView(String txtName, String buildInfo) {
        data.add(new DeviceInfo(txtName, buildInfo));
    }

    /***
     * 当然如何格式化输出可以自由定制,其中,一个Sensor包含了如下字段:
     * <p/>
     * name : 传感器名称
     * vendor : 设备商名称
     * version : 设备版本
     * type : 类型
     * max range : 最大量程
     * resolution : 分辨率
     * power : 功耗
     * min delay : 最小延迟
     *
     * @param sensorType
     * @return
     */
    private String getSensorType(int sensorType) {
        String resultType = null;
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER: //加速度传感器，单位是m/s2，测量应用于设备X、Y、Z轴上的加速度
                resultType = "加速度传感器";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE: //温度传感器，单位是℃
                resultType = "温度传感器";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR: //游戏动作传感器，不收电磁干扰影响
                resultType = "游戏动作传感器";
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR: //地磁旋转矢量传感器，提供手机的旋转矢量，当手机处于休眠状态时，仍可以记录设备的方位
                resultType = "地磁旋转矢量传感器";
                break;
            case Sensor.TYPE_GRAVITY: //重力传感器，单位是m/s2，测量应用于设备X、Y、Z轴上的重力
                resultType = "重力传感器";
                break;
            case Sensor.TYPE_GYROSCOPE: //陀螺仪传感器，单位是rad/s，测量设备x、y、z三轴的角加速度
                resultType = "陀螺仪传感器";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: //未校准陀螺仪传感器，提供原始的，未校准、补偿的陀螺仪数据，用于后期处理和融合定位数据
                resultType = "未校准陀螺仪传感器";
                break;
            case Sensor.TYPE_HEART_RATE: //心率传感器
                resultType = "心率传感器";
                break;
            case Sensor.TYPE_LIGHT: //光线感应传感器，单位lx，检测周围的光线强度
                resultType = "光线感应传感器";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION: //线性加速度传感器，单位是m/s2，该传感器是获取加速度传感器去除重力的影响得到的数据
                resultType = "线性加速度传感器";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD: //磁力传感器，单位是uT(微特斯拉)，测量设备周围三个物理轴（x，y，z）的磁场
                resultType = "磁力传感器";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: //未校准磁力传感器，提供原始的，未校准的磁场数据
                resultType = "未校准磁力传感器";
                break;
            case Sensor.TYPE_PRESSURE: //压力传感器，单位是hPa(百帕斯卡)，返回当前环境下的压强
                resultType = "压力传感器";
                break;
            case Sensor.TYPE_PROXIMITY: //距离传感器，单位是cm，用来测量某个对象到屏幕的距离
                resultType = "距离传感器";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY: //湿度传感器，单位是%，来测量周围环境的相对湿度
                resultType = "湿度传感器";
                break;
            case Sensor.TYPE_ROTATION_VECTOR: //旋转矢量传感器，旋转矢量代表设备的方向
                resultType = "旋转矢量传感器";
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION: //特殊动作触发传感器
                resultType = "特殊动作触发传感器";
                break;
            case Sensor.TYPE_STEP_COUNTER: //计步传感器
                resultType = "计步传感器";
                break;
            case Sensor.TYPE_STEP_DETECTOR: //步行检测传感器，用户每走一步就触发一次事件
                resultType = "步行检测传感器";
                break;
            case Sensor.TYPE_ORIENTATION: //方向传感器,测量设备围绕三个物理轴（x，y，z）的旋转角度
                resultType = "方向传感器";
                break;
            case Sensor.TYPE_TEMPERATURE: //温度传感器，目前已被TYPE_AMBIENT_TEMPERATURE替代
                resultType = "温度传感器";
                break;
//            default:
//                resultType = "UNKNOW";
//                break;
        }
        return resultType;
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

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    /**
     * 获取手机内存大小
     *
     * @return
     */
    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(getContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取当前可用内存大小
     *
     * @return
     */
    private String getAvailMemory() {
        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(getContext().getApplicationContext(), mi.availMem);
    }
}
