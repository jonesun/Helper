package jone.helper.ui.actionBarTabs;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import jone.helper.R;
import jone.helper.lib.util.SystemUtil;

/**
 * Created by jone_admin on 14-3-14.
 */
public class DeviceInfoFragment extends Fragment {
    private LinearLayout centerLayout;
    private Map<Integer, String> versionNameMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVersionMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        centerLayout = (LinearLayout) view.findViewById(R.id.layout_center);
        initData();
    }

    private void initData(){
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String tel = tm.getLine1Number();
        System.out.println("imei: " + imei + ", tel: " + tel); //todo
        String deviceName = SystemUtil.isPad(getActivity()) ? "平板" : "手机";
        addItemView("本机类型", deviceName);
        addItemView("设备制造商", Build.MANUFACTURER);
        addItemView("设备品牌", Build.BRAND);
        addItemView("产品名称: ", Build.PRODUCT);
        addItemView("设备型号", Build.MODEL);
        String versionName = "unknown";
        if(versionNameMap != null && versionNameMap.containsKey(Build.VERSION.SDK_INT)){
            versionName = versionNameMap.get(Build.VERSION.SDK_INT);
        }
        addItemView("系统版本", Build.VERSION.RELEASE + "("  +versionName + ")");
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

        addItemView("设备指令集名称(CPU的类型)", Build.CPU_ABI);
        addItemView("第二个指令集名称", Build.CPU_ABI2);

        addItemView("设备硬件名称", Build.HARDWARE);

        addItemView("无线电固件版本号", Build.RADIO);
        addItemView("设备标签", Build.TAGS);
        addItemView("设备版本类型", Build.TYPE);

        addItemView("设备当前的系统开发代号", Build.VERSION.CODENAME);
        addItemView("系统源代码控制值", Build.VERSION.INCREMENTAL);
        System.out.println("KITKAT: " + Build.VERSION_CODES.KITKAT);
    }

    private void addItemView(String txtName, String buildInfo){
        TextView textView = new TextView(getActivity());
        textView.setText(txtName + ": " + buildInfo);
        centerLayout.addView(textView);
    }

    private void initVersionMap(){
        versionNameMap = new HashMap<Integer, String>();
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
    }

    private void showPhoneInfo(){
        //android.os.Build类中。包括了这样的一些信息。我们可以直接调用 而不需要添加任何的权限和方法。
        System.out.println("获取设备基板名称: " + Build.BOARD);
        System.out.println("获取设备引导程序版本号: " + Build.BOOTLOADER);
        System.out.println("获取设备品牌: " + Build.BRAND);
        System.out.println("获取设备指令集名称（CPU的类型）: " + Build.CPU_ABI);
        System.out.println("获取第二个指令集名称: " + Build.CPU_ABI2);
        System.out.println("获取设备驱动名称: " + Build.DEVICE);
        System.out.println("获取设备显示的版本包（在系统设置中显示为版本号）和ID一样: " + Build.DISPLAY);
        System.out.println("设备的唯一标识。由设备的多个信息拼接合成: " + Build.FINGERPRINT);
        System.out.println("设备硬件名称,一般和基板名称一样（BOARD）: " + Build.HARDWARE);
        System.out.println("设备主机地址: " + Build.HOST);
        System.out.println("设备版本号: " + Build.ID);
        System.out.println("获取手机的型号 设备名称: " + Build.MODEL);
        System.out.println("获取设备制造商: " + Build.MANUFACTURER);
        System.out.println("整个产品的名称: " + Build.PRODUCT);
        System.out.println("无线电固件版本号，通常是不可用的 显示unknown: " + Build.RADIO);
        System.out.println("设备标签。如release-keys 或测试的 test-keys: " + Build.TAGS);
        System.out.println("时间: " + Build.TIME);
        System.out.println("设备版本类型  主要为\"user\" 或\"eng\": " + Build.TYPE);
        System.out.println("设备用户名 基本上都为android-build: " + Build.USER);
        System.out.println("获取系统版本字符串。如4.1.2 或2.2 或2.3等: " + Build.VERSION.RELEASE);
        System.out.println("设备当前的系统开发代号，一般使用REL代替: " + Build.VERSION.CODENAME);
        System.out.println("系统源代码控制值，一个数字或者git hash值: " + Build.VERSION.INCREMENTAL);
        System.out.println("系统的API级别 一般使用下面大的SDK_INT 来查看: " + Build.VERSION.SDK);
        System.out.println("系统的API级别 数字表示: " + Build.VERSION.SDK_INT);
//        System.out.println("类中有所有的已公布的Android版本号: " + Build.VERSION_CODES.BASE);
    }
}
