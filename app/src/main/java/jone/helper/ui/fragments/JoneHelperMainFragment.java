package jone.helper.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import jone.helper.model.customAd.JoneBaiduAd;
import jone.helper.model.weather.entity.Weather;
import jone.helper.model.weather.entity.WeatherData;
import jone.helper.model.weather.entity.WeatherIndex;
import jone.helper.presenter.weather.WeatherPresenter;
import jone.helper.presenter.weather.impl.BaiduWeatherPresenter;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.activities.SelectCityActivity;
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
public class JoneHelperMainFragment extends BaseFragment<HelperMainActivity> implements WeatherView {
    private static final String TAG = JoneHelperMainFragment.class.getSimpleName();
    private ViewGroup layout_arcProgress, layout_weather;
    private LinearLayout layout_ad;
    private TextView txt_calendar, txt_pm25, txt_weather_temperature, txt_weather_weather, txtCapacity, txt_weather;
    private Button btn_city;
    private ImageView image_weather;
    public static final int resultCode = 10001;
    private int[] txt_calendar_ids = new int[]{
            R.id.txt_calendar_0, R.id.txt_calendar_1, R.id.txt_calendar_2,
            R.id.txt_calendar_3, R.id.txt_calendar_4, R.id.txt_calendar_5,
            R.id.txt_calendar_6
    };
    private TextView[] txt_calendars = new TextView[txt_calendar_ids.length];
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
        txt_calendar = findView(view, R.id.txt_calendar);
        btn_city = findView(view, R.id.btn_city);
        image_weather = findView(view, R.id.image_weather);
        txt_pm25 = findView(view, R.id.txt_pm25);
        txt_weather_temperature = findView(view, R.id.txt_weather_temperature);
        txt_weather_weather = findView(view, R.id.txt_weather_weather);
        layout_arcProgress = findView(view, R.id.layout_arcProgress);
        txt_weather = findView(view, R.id.txt_weather);

        layout_weather = findView(view, R.id.layout_weather);

        layout_ad = findView(view, R.id.layout_ad);

        arcStore = findView(view, R.id.arc_store);
        arcProcess = findView(view, R.id.arc_process);
        txtCapacity = findView(view, R.id.txt_capacity);

        for(int i = 0; i < txt_calendar_ids.length; i++){
            txt_calendars[i] = findView(view, txt_calendar_ids[i]);
        }
    }

    @Override
    protected void initViews(View view) {
        showDate();
        JoneBaiduAd.showBDBannerAd(getHostActivity(), layout_ad);
        weatherPresenter = new BaiduWeatherPresenter(this);
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");
        if(Utils.isNetworkAlive(getHostActivity())){
            // 定位初始化
            getLocation();
        }else {
            layout_weather.setVisibility(View.GONE);
            txt_weather.setText("网络连接失败");
        }
        image_weather.setOnClickListener(gotoWeatherFragmentListener);
        txt_pm25.setOnClickListener(gotoWeatherFragmentListener);
        layout_weather.setOnClickListener(gotoWeatherFragmentListener);

        btn_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), SelectCityActivity.class), resultCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getHostActivity().RESULT_OK){
            String result = data.getExtras().getString("result");
            getWeatherByCity(result);
        }
    }

    View.OnClickListener gotoWeatherFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getHostActivity().changeFragment(WeatherFragment.getInstance());
        }
    };

    private LocationClient mLocClient;
    private MyLocationListener myListener = new MyLocationListener();
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
        layout_weather.setVisibility(View.GONE);
    }

    @Override
    public void setWeatherInfo(Weather weather) {
        List<WeatherData> weatherDataList = weather.getWeather_data();
        txt_pm25.setText(weather.getPm25() + " " + WeatherUtil.getPm25String(weather.getPm25()));
        txt_pm25.setBackgroundResource(WeatherUtil.getPm25BgResId(weather.getPm25()));
        if(weatherDataList != null && weatherDataList.size() > 0){
            WeatherData todayWeatherData = weatherDataList.get(0);
            Calendar calendar = Calendar.getInstance();
            String weekDay = FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            if(!todayWeatherData.getDate().contains(weekDay)){
                for(WeatherData weatherData : weatherDataList){
                    if(weatherData.getDate().contains(weekDay)){
                        todayWeatherData = weatherData;
                        break;
                    }
                }
            }
            UmengUtil.get_weather(getHostActivity(), "url", todayWeatherData.getWeather());
            txt_weather_temperature.setText(todayWeatherData.getTemperature());
            txt_weather_weather.setText(todayWeatherData.getWeather() + "(" + todayWeatherData.getWind() + ")");
            int weatherResId = WeatherUtil.getIconResIdByWeather(todayWeatherData.getWeather());
            image_weather.setBackgroundResource(weatherResId);
        }
        StringBuilder weatherStringBuilder = new StringBuilder();
        List<WeatherIndex> weatherIndexList = weather.getIndex();
        if(weatherIndexList != null && weatherIndexList.size() > 0){
            for(WeatherIndex weatherIndex : weatherIndexList){
                weatherStringBuilder.append("\r\n")
                        .append(weatherIndex.getTitle())
                        .append("(").append(weatherIndex.getZs()).append(")").append(": ")
                        .append(weatherIndex.getDes())
                        .append("\r\n");
            }
        }
        if(txt_weather != null){
            txt_weather.setText(weatherStringBuilder.toString());
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location != null){
                String city = location.getCity();
                if(!TextUtils.isEmpty(city)){
                    Log.e(TAG, "city: " + city);
                    UmengUtil.get_location(getHostActivity(), "baiduLocation", city);
                    getWeatherByCity(city.replace("市", ""));
                }
            }
            if(mLocClient != null){
                mLocClient.stop();
                mLocClient.unRegisterLocationListener(this);
            }
        }
    }

    private void getWeatherByCity(String city){
        btn_city.setText(" " + city);
        if(Utils.isNetworkAlive(getHostActivity())){
            if(weatherPresenter != null){
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        }else {
            txt_weather.setText("网络连接失败");
            layout_weather.setVisibility(View.GONE);
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
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        FestivalUtil festivalUtil = new FestivalUtil(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append(calendar.get(Calendar.YEAR)).append("年")
                .append(calendar.get(Calendar.MONTH) + 1).append("月")
                .append(calendar.get(Calendar.DAY_OF_MONTH)).append("日")
                .append(" 周").append(FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1))
                .append("\r\n")
                .append("农历:").append(festivalUtil.getChineseDate()).append(" ");
        ArrayList<String> fest = festivalUtil.getFestVals();
        if(fest.size() > 0){
            stringBuilder.append("今天是: ");
            for(String str:fest){
                stringBuilder.append(str).append(" ");
            }
        }else {
            stringBuilder.append("今天没有节日");
        }
        txt_calendar.setText(stringBuilder.toString());
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(week >= 0 && week < txt_calendars.length){
            txt_calendars[week].setText(calendar.get(Calendar.DAY_OF_MONTH) + "\r\n"
                    + festivalUtil.getChinaDayString());
            txt_calendars[week].setBackgroundResource(R.drawable.today_bg);
            int leftWeek = week - 1;
            Calendar leftCalendar = Calendar.getInstance();
            while (leftWeek >= 0){
                leftCalendar.set(Calendar.DATE, leftCalendar.get(Calendar.DATE) - 1);
                txt_calendars[leftWeek].setText((leftCalendar.get(Calendar.DAY_OF_MONTH)) + "\r\n"
                        + new FestivalUtil(leftCalendar.get(Calendar.YEAR), (leftCalendar.get(Calendar.MONTH) + 1), leftCalendar.get(Calendar.DAY_OF_MONTH)).getChinaDayString());
                leftWeek --;
            }
            Calendar rightCalendar = Calendar.getInstance();
            int rightWeek = week + 1;
            while (rightWeek < txt_calendars.length){
                rightCalendar.set(Calendar.DATE, rightCalendar.get(Calendar.DATE) + 1);
                txt_calendars[rightWeek].setText((rightCalendar.get(Calendar.DAY_OF_MONTH)) + "\r\n"
                        + new FestivalUtil(rightCalendar.get(Calendar.YEAR), (rightCalendar.get(Calendar.MONTH) + 1), rightCalendar.get(Calendar.DAY_OF_MONTH)).getChinaDayString());
                rightWeek++;
            }
        }
    }
}
