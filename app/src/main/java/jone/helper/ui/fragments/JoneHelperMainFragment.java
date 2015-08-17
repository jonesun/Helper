package jone.helper.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jone.helper.R;
import jone.helper.lib.util.Utils;
import jone.helper.model.SDCardInfo;
import jone.helper.model.weather.entity.Weather;
import jone.helper.model.weather.entity.WeatherData;
import jone.helper.model.weather.entity.WeatherIndex;
import jone.helper.presenter.weather.WeatherPresenter;
import jone.helper.presenter.weather.impl.BaiduWeatherPresenter;
import jone.helper.thridAd.Jone_AppConnectAd;
import jone.helper.ui.activities.AppManagerActivity;
import jone.helper.ui.main.MenuActivity;
import jone.helper.ui.view.WeatherView;
import jone.helper.ui.widget.circleprogress.ArcProgress;
import jone.helper.util.AppUtil;
import jone.helper.util.FestivalUtil;
import jone.helper.util.StorageUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * Created by jone.sun on 2015/8/17.
 */
public class JoneHelperMainFragment extends BaseFragment<MenuActivity> implements WeatherView {
    private static final String TAG = JoneHelperMainFragment.class.getSimpleName();
    private ViewGroup layout_arcProgress;
    private LinearLayout layout_top, layout_ad;
    private TextView txt_weather, txt_date, txt_festival, txtCapacity;
    private ArcProgress arcStore, arcProcess;

    private Dialog loadingDialog;

    private WeatherPresenter weatherPresenter;

    private Timer timer;
    private Timer timer2;

    private static JoneHelperMainFragment instance = null;
    public static JoneHelperMainFragment getInstance(){
        if(instance == null){
            instance = new JoneHelperMainFragment();
        }
        return instance;
    }
    public JoneHelperMainFragment() {
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_jone_helper_main;
    }

    @Override
    protected void findViews(View view) {
        layout_arcProgress = findView(view, R.id.layout_arcProgress);
        layout_top = findView(view, R.id.layout_top);
        txt_weather = findView(view, R.id.txt_weather);
        txt_date = findView(view, R.id.txt_date);
        txt_festival = findView(view, R.id.txt_festival);
        layout_ad = findView(view, R.id.layout_ad);

        arcStore = findView(view, R.id.arc_store);
        arcProcess = findView(view, R.id.arc_process);
        txtCapacity = findView(view, R.id.txt_capacity);
    }

    @Override
    protected void initViews(View view) {
        showDate();
        Jone_AppConnectAd.showMinAd(getHostActivity(), layout_ad);
        weatherPresenter = new BaiduWeatherPresenter(this);
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");
        if(Utils.isNetworkAlive(getHostActivity())){
            // 定位初始化
            getLocation();
        }else {
            txt_weather.setText("网络连接失败");
        }

        layout_arcProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().startActivity(new Intent(getHostActivity(), AppManagerActivity.class));
            }
        });

        layout_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().changeFragment(WeatherFragment.getInstance());
            }
        });
    }

    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private void getLocation(){
        // 定位初始化
        mLocClient = new LocationClient(getHostActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        //option.setOpenGps(true);// 打开gps
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(1000); //设置发起定位请求的间隔时间为5000ms
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    @Override
    public void showError(String reason) {
        Toast.makeText(getHostActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
        txt_weather.setText("网络连接失败");
    }

    @Override
    public void setWeatherInfo(Weather weather) {
        List<WeatherData> weatherDataList = weather.getWeather_data();
        StringBuffer weatherStringBuffer = new StringBuffer();
        String city = weather.getCurrentCity();
        weatherStringBuffer.append(city).append("\r\n");
        String pm25String = WeatherUtil.getPm25String(weather.getPm25());
        weatherStringBuffer.append("pm2.5: ").append(weather.getPm25()).append("(").append(pm25String).append(")").append("\r\n");
        if(weatherDataList != null && weatherDataList.size() > 0){
            WeatherData todayWeatherData = weatherDataList.get(0);
            UmengUtil.get_weather(getHostActivity(), "url", todayWeatherData.getWeather());
            weatherStringBuffer.append("温度: ").append(todayWeatherData.getTemperature()).append("\r\n")
                    .append("天气: ").append(todayWeatherData.getWeather()).append("\r\n")
                    .append("风度: ").append(todayWeatherData.getWind());
        }
        List<WeatherIndex> weatherIndexList = weather.getIndex();
        if(weatherIndexList != null && weatherIndexList.size() > 0){
            for(WeatherIndex weatherIndex : weatherIndexList){
                weatherStringBuffer.append("\r\n")
                        .append("\r\n")
                        .append(weatherIndex.getTitle())
                        .append("(").append(weatherIndex.getZs()).append(")").append(": ")
                        .append(weatherIndex.getDes());
            }
        }
        if(txt_weather != null){
            txt_weather.setText(weatherStringBuffer.toString());
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location != null && location.getCity() != null){
                Log.e(TAG, "city: " + location.getCity());
                getWeatherByCity(location.getCity().replace("市", ""));
            }
            if(mLocClient != null){
                mLocClient.stop();
                mLocClient.unRegisterLocationListener(this);
            }
        }
    }

    private void getWeatherByCity(String city){
        if(Utils.isNetworkAlive(getHostActivity())){
            if(weatherPresenter != null){
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        }else {
            txt_weather.setText("网络连接失败");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fillData();
    }

    private void fillData(){
        timer = null;
        timer2 = null;
        timer = new Timer();
        timer2 = new Timer();


        long l = AppUtil.getAvailMemory(getHostActivity());
        long y = AppUtil.getTotalMemory(getHostActivity());
        final double x = (((y - l) / (double) y) * 100);
        //   arcProcess.setProgress((int) x);

        arcProcess.setProgress(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (arcProcess.getProgress() >= (int) x) {
                            timer.cancel();
                        } else {
                            arcProcess.setProgress(arcProcess.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);

        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(getHostActivity());

        long nAvailaBlock;
        long TotalBlocks;
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }

        final double percentStore = (((TotalBlocks - nAvailaBlock) / (double) TotalBlocks) * 100);

        txtCapacity.setText(StorageUtil.convertStorage(TotalBlocks - nAvailaBlock) + "/" + StorageUtil.convertStorage(TotalBlocks));
        arcStore.setProgress(0);

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (arcStore.getProgress() >= (int) percentStore) {
                            timer2.cancel();
                        } else {
                            arcStore.setProgress(arcStore.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if(mLocClient != null){
            mLocClient.stop();
        }
        if(timer != null){
            timer.cancel();
        }
        if(timer2 != null){
            timer2.cancel();
        }
    }

    private void showDate(){
        Calendar calendar = Calendar.getInstance();
        FestivalUtil festivalUtil = new FestivalUtil(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));

        txt_date.setText(calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + "周" + FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1)  +" 农历:" + festivalUtil.getChineseDate());
        ArrayList<String> fest = festivalUtil.getFestVals();
        StringBuilder festival = new StringBuilder();
        if(fest.size() > 0){
            for(String str:fest){
                festival.append(str).append("(").append(FestivalUtil.getPinYin(str).trim()).append(")").append(" ");
                System.out.println(str + "(" + FestivalUtil.getPinYin(str, "_").trim() + ")");
            }
            txt_festival.setText("今天是: " + festival);
        }else {
            txt_festival.setText("今天没有节日");
        }
    }
}
