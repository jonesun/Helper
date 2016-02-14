package jone.helper.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import jone.helper.R;
import jone.helper.bean.DateInfo;
import jone.helper.bean.HistoryToday;
import jone.helper.bean.HomeMainCalendarBean;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.Utils;
import jone.helper.model.AMapLocationTool;
import jone.helper.model.MemoryStoreProgressTool;
import jone.helper.model.calendar.CalendarActivity;
import jone.helper.mvp.model.HistoryTodayModel;
import jone.helper.mvp.model.load.Callback;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.mvp.model.weather.entity.WeatherIndex;
import jone.helper.mvp.presenter.weather.WeatherPresenter;
import jone.helper.mvp.presenter.weather.impl.BaiduWeatherPresenter;
import jone.helper.mvp.view.weather.WeatherView;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.activities.SelectCityActivity;
import jone.helper.ui.adapter.MainCalendarAdapter;
import jone.helper.ui.fragments.base.BaseFragment;
import jone.helper.ui.widget.circleprogress.ArcProgress;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * Created by jone.sun on 2015/8/17.
 */
public class HelperMainFragment extends BaseFragment<HelperMainActivity> implements WeatherView {
    private static final String TAG = HelperMainFragment.class.getSimpleName();
    private ViewGroup layout_weather;
    private LinearLayout layout_ad;
    private View layout_history_today;
    private TextSwitcher text_switcher_history_today;
    private TextView txt_calendar, txt_pm25, txt_weather_temperature,
            txt_weather_weather, txt_weather_index, txt_capacity,
            txt_weather_1, txt_weather_2, txt_weather_3;
    private Button btn_city;
    private RecyclerView recyclerViewCalendar;
    private ArcProgress arc_store, arc_process;
    private ImageView image_weather;
    public static final int resultCode = 10001;

    private Dialog loadingDialog;
    private MemoryStoreProgressTool memoryStoreProgressTool;

    private WeatherPresenter weatherPresenter;

    private static HelperMainFragment instance = null;

    public static HelperMainFragment getInstance() {
        if (instance == null) {
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
        layout_history_today = findView(view, R.id.layout_history_today);
        text_switcher_history_today = findView(view, R.id.text_switcher_history_today);
        txt_calendar = findView(view, R.id.txt_calendar);
        btn_city = findView(view, R.id.btn_city);
        image_weather = findView(view, R.id.image_weather);
        txt_pm25 = findView(view, R.id.txt_pm25);
        txt_weather_temperature = findView(view, R.id.txt_weather_temperature);
        txt_weather_weather = findView(view, R.id.txt_weather_weather);

        layout_weather = findView(view, R.id.layout_weather);

        txt_weather_1 = findView(view, R.id.txt_weather_1);
        txt_weather_2 = findView(view, R.id.txt_weather_2);
        txt_weather_3 = findView(view, R.id.txt_weather_3);

        layout_ad = findView(view, R.id.layout_ad);
        recyclerViewCalendar = findView(view, R.id.recyclerViewCalendar);

        arc_store = findView(view, R.id.arc_store);
        arc_process = findView(view, R.id.arc_process);
        txt_capacity = findView(view, R.id.txt_capacity);

        txt_weather_index = findView(view, R.id.txt_weather_index);

        DateInfo dateInfo = getDateInfo();
        txt_calendar.setText(dateInfo.getDataStr());
        MainCalendarAdapter mainCalendarAdapter = new MainCalendarAdapter(getHostActivity());
        List<HomeMainCalendarBean> homeMainCalendarBeanList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            HomeMainCalendarBean homeMainCalendarBean = new HomeMainCalendarBean();
            homeMainCalendarBean.setLabel(FestivalUtil.getWeekDay(i));
            homeMainCalendarBean.setCalendar(dateInfo.getCalendars()[i]);
            if (dateInfo.getTodayIndex() == i) {
                homeMainCalendarBean.setIsToday(true);
            }
            homeMainCalendarBeanList.add(homeMainCalendarBean);
        }
        mainCalendarAdapter.setDataList(homeMainCalendarBeanList);
        recyclerViewCalendar.setAdapter(mainCalendarAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCalendar.setLayoutManager(mLayoutManager);
        recyclerViewCalendar.setFocusableInTouchMode(false);
//        JoneBaiduAd.showBDBannerAd(getHostActivity(), layout_ad);
        weatherPresenter = new BaiduWeatherPresenter(this);
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");
        memoryStoreProgressTool = new MemoryStoreProgressTool();
        if (Utils.isNetworkAlive(getHostActivity())) {
            // 定位初始化
            getLocation();
            showHistoryTodayView();
        } else {
            showFail();
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

        txt_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getHostActivity(), CalendarActivity.class));
            }
        });

        arc_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity(), JoneAppManagerActivity.class);
                intent.putExtra(JoneAppManagerActivity.KEY_CURRENT_INDEX, 2);
                startActivity(intent);
            }
        });
        arc_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity(), JoneAppManagerActivity.class);
                intent.putExtra(JoneAppManagerActivity.KEY_CURRENT_INDEX, 0);
                startActivity(intent);
            }
        });
    }

    private void showHistoryTodayView() {
        new HistoryTodayModel(10).loadData(1, new Callback<List<HistoryToday>>() {
            @Override
            public void onComplete(@ResultCode int resultCode, String message, final List<HistoryToday> data) {
                if (data != null && data.size() > 0) {
                    if (layout_history_today.getVisibility() == View.GONE) {
                        layout_history_today.setVisibility(View.VISIBLE);
                        text_switcher_history_today.setFactory(new ViewSwitcher.ViewFactory() {
                            @Override
                            public View makeView() {
                                TextView tv = new TextView(getContext());
                                return tv;
                            }
                        });
                        Animation in = AnimationUtils.loadAnimation(getContext(),
                                android.R.anim.fade_in);
                        Animation out = AnimationUtils.loadAnimation(getContext(),
                                android.R.anim.fade_out);
                        text_switcher_history_today.setInAnimation(in);
                        text_switcher_history_today.setOutAnimation(out);
                        text_switcher_history_today.post(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    HistoryToday historyToday = data.get(new Random().nextInt(data.size()));
                                    text_switcher_history_today.setText(getString(R.string.history_today, historyToday.getYear(), historyToday.getTitle()));
                                    text_switcher_history_today.postDelayed(this, 10000);
                                }catch (Exception e){

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void showFail() {
        layout_weather.setVisibility(View.GONE);
        txt_weather_index.setText(Html.fromHtml("连接失败<u>重试</u>"));
        txt_weather_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().getThemeTool().refreshTheme(getHostActivity());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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

    private void getLocation() {
        AMapLocationTool.getInstance().startLocation(AMapLocationClientOption.AMapLocationMode.Battery_Saving,
                new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation != null) {
                            Log.e("AMapLocationTool", "onLocationChanged>>" + GsonUtils.toJson(aMapLocation));
                            String city = aMapLocation.getCity();
                            if (!TextUtils.isEmpty(city)) {
                                Log.e(TAG, "city: " + city);
                                UmengUtil.get_location(getHostActivity(), "baiduLocation", city);
                                getWeatherByCity(city.replace("市", ""));
                            } else {
                                showFail();
                            }
                        }
                        AMapLocationTool.getInstance().stopLocation();
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
        txt_weather_index.setText(Html.fromHtml("网络连接失败<u>重试</u>"));
        txt_weather_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().getThemeTool().refreshTheme(getHostActivity());
            }
        });
        layout_weather.setVisibility(View.GONE);
    }

    @Override
    public void setWeatherInfo(Weather weather) {
        if (weather == null) {
            return;
        }
        List<WeatherData> weatherDataList = weather.getWeather_data();
        txt_pm25.setText(weather.getPm25() + " " + WeatherUtil.getPm25String(weather.getPm25()));
        txt_pm25.setBackgroundResource(R.drawable.bg_weather_pm25);
        int level = 0;
        try {
            level = Integer.parseInt(weather.getPm25());
        } catch (Exception e) {
            level = 0;
        }
        txt_pm25.getBackground().setLevel(level);
        if (weatherDataList != null && weatherDataList.size() > 2) {
            WeatherData todayWeatherData = weatherDataList.get(0);
            Calendar calendar = Calendar.getInstance();
            String weekDay = FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            if (!todayWeatherData.getDate().contains(weekDay)) {
                for (WeatherData weatherData : weatherDataList) {
                    if (weatherData.getDate().contains(weekDay)) {
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

            WeatherData weatherData1 = weatherDataList.get(1);
            txt_weather_1.setText("明天 " + weatherData1.getWeather() + "\r\n"
                    + weatherData1.getTemperature() + "\r\n" +
                    weatherData1.getWind());

            WeatherData weatherData2 = weatherDataList.get(2);
            txt_weather_2.setText("后天 " + weatherData2.getWeather() + "\r\n"
                    + weatherData2.getTemperature() + "\r\n" +
                    weatherData2.getWind());

            WeatherData weatherData3 = weatherDataList.get(3);
            txt_weather_3.setText(weatherData3.getDate() + " " + weatherData3.getWeather() + "\r\n"
                    + weatherData3.getTemperature() + "\r\n" +
                    weatherData3.getWind());
        }
        StringBuilder weatherStringBuilder = new StringBuilder();
        List<WeatherIndex> weatherIndexList = weather.getIndex();
        if (weatherIndexList != null && weatherIndexList.size() > 0) {
            for (WeatherIndex weatherIndex : weatherIndexList) {
                weatherStringBuilder.append("\r\n")
                        .append(weatherIndex.getTitle())
                        .append("(").append(weatherIndex.getZs()).append(")").append(": ")
                        .append(weatherIndex.getDes())
                        .append("\r\n");
            }
        }
        txt_weather_index.setText(weatherStringBuilder.toString());
        txt_weather_index.setOnClickListener(null);
    }

    private void getWeatherByCity(String city) {
        btn_city.setText(" " + city);
        if (Utils.isNetworkAlive(getHostActivity())) {
            if (weatherPresenter != null) {
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        } else {
            txt_weather_index.setText(Html.fromHtml("网络连接失败<u>重试</u>"));
            txt_weather_index.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHostActivity().getThemeTool().refreshTheme(getHostActivity());
                }
            });
            layout_weather.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (memoryStoreProgressTool != null) {
            memoryStoreProgressTool.showMemoryProgress(getHostActivity(), arc_process);
            memoryStoreProgressTool.showStoreProgress(getHostActivity(), arc_store, txt_capacity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AMapLocationTool.getInstance().destroy();
        if (memoryStoreProgressTool != null) {
            memoryStoreProgressTool.close();
        }
    }

    private DateInfo getDateInfo() {
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
        if (fest.size() > 0) {
            stringBuilder.append("今天是: ");
            for (String str : fest) {
                stringBuilder.append(str).append(" ");
            }
        } else {
            stringBuilder.append("今天没有节日");
        }
        dateInfo.setDataStr(stringBuilder.toString());
        String[] calendars = new String[7];
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (week >= 0 && week < calendars.length) {
            calendars[week] = calendar.get(Calendar.DAY_OF_MONTH) + "\r\n"
                    + festivalUtil.getChinaDayString();
            dateInfo.setTodayIndex(week);
            int leftWeek = week - 1;
            Calendar leftCalendar = Calendar.getInstance();
            while (leftWeek >= 0) {
                leftCalendar.set(Calendar.DATE, leftCalendar.get(Calendar.DATE) - 1);
                calendars[leftWeek] = (leftCalendar.get(Calendar.DAY_OF_MONTH)) + "\r\n"
                        + new FestivalUtil(leftCalendar.get(Calendar.YEAR), (leftCalendar.get(Calendar.MONTH) + 1), leftCalendar.get(Calendar.DAY_OF_MONTH)).getChinaDayString();
                leftWeek--;
            }
            Calendar rightCalendar = Calendar.getInstance();
            int rightWeek = week + 1;
            while (rightWeek < calendars.length) {
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
