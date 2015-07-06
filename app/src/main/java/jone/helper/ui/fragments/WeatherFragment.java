package jone.helper.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.R;
import jone.helper.adapter.WeatherAdapter;
import jone.helper.callbacks.CommonListener;
import jone.helper.lib.util.Utils;
import jone.helper.model.weather.entity.Weather;
import jone.helper.model.weather.entity.WeatherData;
import jone.helper.model.weather.entity.WeatherIndex;
import jone.helper.presenter.WeatherPresenter;
import jone.helper.presenter.impl.BaiduWeatherPresenter;
import jone.helper.thridAd.Jone_AppConnectAd;
import jone.helper.ui.BaiduMapActivity;
import jone.helper.ui.SelectCityActivity;
import jone.helper.ui.main.MenuActivity;
import jone.helper.ui.view.WeatherView;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class WeatherFragment extends BaseFragment<MenuActivity> implements WeatherView {
    private static final String TAG = WeatherFragment.class.getSimpleName();
    private LinearLayout layout_top, layout_ad;
    private TextView txt_city, txt_pm25, txt_date, txt_festival;
    private RecyclerView mRecyclerView;
    private WeatherAdapter weatherAdapter;
    private Dialog loadingDialog;

    private WeatherPresenter weatherPresenter;

    private List<WeatherData> weatherDataList = new ArrayList<>();

    public static final int resultCode = 10001;
    private static WeatherFragment instance = null;
    public static WeatherFragment getInstance(){
        if(instance == null){
            instance = new WeatherFragment();
        }
        return instance;
    }
    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void findViews(View view) {
        layout_top = findView(view, R.id.layout_top);
        txt_city = findView(view, R.id.txt_city);
        txt_pm25 = findView(view, R.id.txt_pm25);
        txt_date = findView(view, R.id.txt_date);
        txt_festival = findView(view, R.id.txt_festival);
        mRecyclerView = findView(view, R.id.weather_list);
        layout_ad = findView(view, R.id.layout_ad);
    }

    @Override
    protected void initViews(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        weatherAdapter = new WeatherAdapter(getActivity(), weatherDataList);
        mRecyclerView.setAdapter(weatherAdapter);
        showDate();
        Jone_AppConnectAd.showMinAd(getHostActivity(), layout_ad);
        showEggs();
        txt_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), SelectCityActivity.class), resultCode);
            }
        });

        weatherPresenter = new BaiduWeatherPresenter(this); //传入WeatherView
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");

        // 定位初始化
        getLocation();
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

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location != null && location.getCity() != null){
                Log.e("sss", "city: " + location.getCity());
                getWeatherByCity(location.getCity().replace("市", ""));
            }
            if(mLocClient != null){
                mLocClient.stop();
                mLocClient.unRegisterLocationListener(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if(mLocClient != null){
            mLocClient.stop();
        }
    }

    private boolean isFirst = true;
    @Override
    public void onResume() {
        super.onResume();
        if(isFirst){
            getLocation();
            isFirst = false;
        }
    }

    private void getWeatherByCity(String city){
        if(Utils.isNetworkAlive(getHostActivity())){
            if(weatherPresenter != null){
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        }else {
            txt_city.setText("网络连接失败");
        }
    }

    long[] mHits = new long[3];
    private void showEggs(){
        if(layout_top!= null){
            layout_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                    mHits[mHits.length-1] = SystemClock.uptimeMillis();
                    if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                        startActivity(new Intent(getActivity(), BaiduMapActivity.class));
                    }
                }
            });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getHostActivity().RESULT_OK){
            String result = data.getExtras().getString("result");
            getWeatherByCity(result);
        }
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
        //Do something
        Toast.makeText(getHostActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setWeatherInfo(Weather weather) {
        if(txt_pm25 != null){
            List<WeatherIndex> weatherIndexList = weather.getIndex();
            String pm25String = WeatherUtil.getPm25String(weather.getPm25());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pm2.5: ").append(weather.getPm25()).append("(").append(pm25String).append(")");
            if(weatherIndexList != null && weatherIndexList.size() > 0){
                for(WeatherIndex weatherIndex : weatherIndexList){
                    stringBuilder.append("\r\n").append(weatherIndex.getTitle())
                            .append("(").append(weatherIndex.getZs()).append(")").append(": ")
                            .append(weatherIndex.getDes());
                }
            }
            txt_pm25.setText(stringBuilder.toString());
        }
        weatherDataList = weather.getWeather_data();
        if(weatherDataList != null && weatherDataList.size() > 0 && weatherAdapter != null){
            weatherAdapter.setWeatherDataList(weatherDataList);
            weatherAdapter.notifyDataSetChanged();
            UmengUtil.get_weather(getHostActivity(), "url", weatherDataList.get(0).getWeather());
        }

        String city = weather.getCurrentCity();
        if(txt_city != null){
            txt_city.setText(Html.fromHtml("<u>" + city + "</u>"));
        }
    }
}
