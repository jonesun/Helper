package jone.helper.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.lightsky.infiniteindicator.InfiniteIndicatorLayout;
import cn.lightsky.infiniteindicator.slideview.BaseSliderView;
import cn.lightsky.infiniteindicator.slideview.DefaultSliderView;
import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.DateInfo;
import jone.helper.bean.HomeMainCalendarBean;
import jone.helper.databinding.FragmentJoneHelperMainBinding;
import jone.helper.lib.util.SystemUtil;
import jone.helper.lib.util.Utils;
import jone.helper.model.BaiduLocationTool;
import jone.helper.model.MemoryStoreProgressTool;
import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.model.bing.OnBingPicturesListener;
import jone.helper.model.calendar.CalendarActivity;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.mvp.model.weather.entity.WeatherIndex;
import jone.helper.mvp.presenter.weather.WeatherPresenter;
import jone.helper.mvp.presenter.weather.impl.BaiduWeatherPresenter;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.activities.ZoomImageViewActivity;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.activities.SelectCityActivity;
import jone.helper.ui.adapter.MainCalendarAdapter;
import jone.helper.ui.fragments.base.DataBindingBaseFragment;
import jone.helper.mvp.view.weather.WeatherView;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * Created by jone.sun on 2015/8/17.
 */
public class HelperMainFragment extends DataBindingBaseFragment<HelperMainActivity, FragmentJoneHelperMainBinding> implements WeatherView {
    private static final String TAG = HelperMainFragment.class.getSimpleName();
    private ViewGroup layout_weather;
    private LinearLayout layout_ad;
    private TextView txt_calendar, txt_pm25, txt_weather_temperature, txt_weather_weather;
    private Button btn_city;
    private ImageView image_weather;
    public static final int resultCode = 10001;

    private Dialog loadingDialog;
    private BaiduLocationTool baiduLocationTool;
    private MemoryStoreProgressTool memoryStoreProgressTool;

    private WeatherPresenter weatherPresenter;

    private static HelperMainFragment instance = null;
    public static HelperMainFragment getInstance(){
        if(instance == null){
            instance = new HelperMainFragment();
        }
        return instance;
    }
    public HelperMainFragment() {
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_jone_helper_main;
    }

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    protected void findViews(View view) {
        txt_calendar = findView(view, R.id.txt_calendar);
        btn_city = findView(view, R.id.btn_city);
        image_weather = findView(view, R.id.image_weather);
        txt_pm25 = findView(view, R.id.txt_pm25);
        txt_weather_temperature = findView(view, R.id.txt_weather_temperature);
        txt_weather_weather = findView(view, R.id.txt_weather_weather);

        layout_weather = findView(view, R.id.layout_weather);

        layout_ad = findView(view, R.id.layout_ad);
    }

    @Override
    public void initViews(FragmentJoneHelperMainBinding viewDataBinding) {
        findViews(viewDataBinding.getRoot());
        DateInfo dateInfo = getDateInfo();
        txt_calendar.setText(dateInfo.getDataStr());
        MainCalendarAdapter mainCalendarAdapter = new MainCalendarAdapter(getHostActivity());
        List<HomeMainCalendarBean> homeMainCalendarBeanList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            HomeMainCalendarBean homeMainCalendarBean = new HomeMainCalendarBean();
            homeMainCalendarBean.setLabel(FestivalUtil.getWeekDay(i));
            homeMainCalendarBean.setCalendar(dateInfo.getCalendars()[i]);
            if(dateInfo.getTodayIndex() == i){
                homeMainCalendarBean.setIsToday(true);
            }
            homeMainCalendarBeanList.add(homeMainCalendarBean);
        }
        mainCalendarAdapter.setDataList(homeMainCalendarBeanList);
        viewDataBinding.recyclerViewCalendar.setAdapter(mainCalendarAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewDataBinding.recyclerViewCalendar.setLayoutManager(mLayoutManager);
        viewDataBinding.recyclerViewCalendar.setFocusableInTouchMode(false);
//        JoneBaiduAd.showBDBannerAd(getHostActivity(), layout_ad);
        weatherPresenter = new BaiduWeatherPresenter(this);
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");
        baiduLocationTool = new BaiduLocationTool();
        memoryStoreProgressTool = new MemoryStoreProgressTool();
        if(Utils.isNetworkAlive(getHostActivity())){
            // 定位初始化
            getLocation();
        }else {
            layout_weather.setVisibility(View.GONE);
            viewDataBinding.txtWeatherIndex.setText("网络连接失败");
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

        viewDataBinding.txtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getHostActivity(), CalendarActivity.class));
            }
        });

        getViewDataBinding().arcProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity(), JoneAppManagerActivity.class);
                intent.putExtra("currentIndex", 2);
                startActivity(intent);
            }
        });
        getViewDataBinding().arcStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity(), JoneAppManagerActivity.class);
                intent.putExtra("currentIndex", 0);
                startActivity(intent);
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

    private void getLocation(){
        baiduLocationTool.getLocation(getHostActivity(), new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                // map view 销毁后不在处理新接收的位置
                if (bdLocation != null) {
                    String city = bdLocation.getCity();
                    if (!TextUtils.isEmpty(city)) {
                        Log.e(TAG, "city: " + city);
                        UmengUtil.get_location(getHostActivity(), "baiduLocation", city);
                        getWeatherByCity(city.replace("市", ""));
                    }
                }
                if (baiduLocationTool != null) {
                    baiduLocationTool.close();
                }
            }
        });
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
        getViewDataBinding().txtWeatherIndex.setText("网络连接失败");
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
        getViewDataBinding().txtWeatherIndex.setText(weatherStringBuilder.toString());
    }

    private void getWeatherByCity(String city){
        btn_city.setText(" " + city);
        if(Utils.isNetworkAlive(getHostActivity())){
            if(weatherPresenter != null){
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        }else {
            getViewDataBinding().txtWeatherIndex.setText("网络连接失败");
            layout_weather.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(memoryStoreProgressTool != null){
            memoryStoreProgressTool.showMemoryProgress(getHostActivity(), getViewDataBinding().arcProcess);
            memoryStoreProgressTool.showStoreProgress(getHostActivity(), getViewDataBinding().arcStore, getViewDataBinding().txtCapacity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (baiduLocationTool != null) {
            baiduLocationTool.close();
        }
        if(memoryStoreProgressTool != null){
            memoryStoreProgressTool.close();
        }
    }

    private DateInfo getDateInfo(){
        DateInfo dateInfo = new DateInfo();
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
        dateInfo.setDataStr(stringBuilder.toString());
        String[] calendars = new String[7];
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(week >= 0 && week < calendars.length){
            calendars[week] = calendar.get(Calendar.DAY_OF_MONTH) + "\r\n"
                    + festivalUtil.getChinaDayString();
            dateInfo.setTodayIndex(week);
            int leftWeek = week - 1;
            Calendar leftCalendar = Calendar.getInstance();
            while (leftWeek >= 0){
                leftCalendar.set(Calendar.DATE, leftCalendar.get(Calendar.DATE) - 1);
                calendars[leftWeek] = (leftCalendar.get(Calendar.DAY_OF_MONTH)) + "\r\n"
                        + new FestivalUtil(leftCalendar.get(Calendar.YEAR), (leftCalendar.get(Calendar.MONTH) + 1), leftCalendar.get(Calendar.DAY_OF_MONTH)).getChinaDayString();
                leftWeek --;
            }
            Calendar rightCalendar = Calendar.getInstance();
            int rightWeek = week + 1;
            while (rightWeek < calendars.length){
                rightCalendar.set(Calendar.DATE, rightCalendar.get(Calendar.DATE) + 1);
                calendars[rightWeek] = (rightCalendar.get(Calendar.DAY_OF_MONTH)) + "\r\n"
                        + new FestivalUtil(rightCalendar.get(Calendar.YEAR), (rightCalendar.get(Calendar.MONTH) + 1), rightCalendar.get(Calendar.DAY_OF_MONTH)).getChinaDayString();
                rightWeek++;
            }
        }
        dateInfo.setCalendars(calendars);
        return dateInfo;
    }
}
