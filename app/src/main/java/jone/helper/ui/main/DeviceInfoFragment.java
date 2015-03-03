package jone.helper.ui.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jone.helper.R;
import jone.helper.lib.util.SystemUtil;

public class DeviceInfoFragment extends Fragment {
    private static final String TAG = DeviceInfoFragment.class.getSimpleName();
    private Map<Integer, String> versionNameMap;
    public List<String> data = new ArrayList<>();

    LinearLayoutManager layoutManager;

    public DeviceInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        initVersionMap();
        initData();
        recyclerView.setAdapter(new MyAdapter(data));
    }

    private void initData(){
        data = new ArrayList<>();
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
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
        if(versionNameMap != null && versionNameMap.containsKey(Build.VERSION.SDK_INT)){
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
        data.add(txtName + ": " + buildInfo);
    }

    private void initVersionMap(){
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
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> data;

        public MyAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_device_info_layout, viewGroup, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.text.setText(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

}
