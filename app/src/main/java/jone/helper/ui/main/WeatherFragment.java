package jone.helper.ui.main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.AppConnect;
import jone.helper.Constants;
import jone.helper.R;
import jone.helper.adapter.WeatherAdapter;
import jone.helper.bean.Weather;
import jone.helper.bean.WeatherData;
import jone.helper.callbacks.CommonListener;
import jone.helper.lib.util.Utils;
import jone.helper.ui.EggsActivity;
import jone.helper.ui.SelectCityActivity;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
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

    private TextView txt_city, txt_pm25;
    private static final String TAG = WeatherFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private WeatherAdapter weatherAdapter;
    private List<WeatherData> weatherDataList = new ArrayList<>();
    private MenuActivity activity;
    private String weatherUrl;
    public TextView txt_date;
    public TextView txt_festival;
    private LinearLayout layout_ad, layout_top;

    private static final int WHAT_UPDATE_CITY_VIEW = 1000;
    private static final int WHAT_UPDATE_WEATHER_VIEW = 1001;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_UPDATE_CITY_VIEW:
                    if(msg.obj != null){
                        String city = msg.obj.toString();
                        if(txt_city != null){
                            txt_city.setText(Html.fromHtml("<u>" + city + "</u>"));
                        }
                    }else {
                        if(txt_city != null){
                            txt_city.setText("城市获取失败");
                        }
                    }
                    break;
                case WHAT_UPDATE_WEATHER_VIEW:
                    if(msg.obj != null && msg.obj instanceof Weather){
                        Weather weather = (Weather) msg.obj;
                        if(txt_pm25 != null){
                            String pm25String = WeatherUtil.getPm25String(weather.getPm25());
                            if(pm25String != null){
                                txt_pm25.setText("pm2.5: " + weather.getPm25() + "(" + pm25String + ")");
                            }else {
                                txt_pm25.setText("pm2.5: " + weather.getPm25());
                            }

                        }
                        weatherDataList = weather.getWeather_data();
                        if(weatherDataList != null && weatherDataList.size() > 0 && weatherAdapter != null){
                            weatherAdapter.setWeatherDataList(weatherDataList);
                            weatherAdapter.notifyDataSetChanged();
                            UmengUtil.get_weather(activity, weatherUrl, weatherDataList.get(0).getWeather());
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MenuActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout_top = (LinearLayout) view.findViewById(R.id.layout_top);
        txt_city = (TextView) view.findViewById(R.id.txt_city);
        txt_pm25 = (TextView) view.findViewById(R.id.txt_pm25);

        txt_date = (TextView) view.findViewById(R.id.txt_date);
        txt_festival = (TextView) view.findViewById(R.id.txt_festival);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.weather_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        weatherAdapter = new WeatherAdapter(getActivity(), weatherDataList);
        mRecyclerView.setAdapter(weatherAdapter);
        showDate();
        try{
            AppConnect.getInstance(activity).setAdBackColor(Color.WHITE);
            AppConnect.getInstance(activity).setAdForeColor(Color.BLACK);
            layout_ad = (LinearLayout) view.findViewById(R.id.layout_ad);
            AppConnect.getInstance(activity).showMiniAd(activity, layout_ad, 8);
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }

        showEggs();

        txt_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), SelectCityActivity.class), resultCode);
            }
        });
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
                        startActivity(new Intent(getActivity(), EggsActivity.class));
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
        StringBuffer festival = new StringBuffer();
        if(fest.size() > 0){
            for(String str:fest){
                festival.append(str + "(" + FestivalUtil.getPinYin(str).trim() + ")" +" ");
                System.out.println(str + "(" + FestivalUtil.getPinYin(str, "_").trim() + ")");
            }
            txt_festival.setText("今天是: " + festival);
        }else {
            txt_festival.setText("今天没有节日");
        }
    }

    private boolean isFirst = true;
    @Override
    public void onResume() {
        super.onResume();
        if(isFirst){
            getWeather();
            isFirst = false;
        }

    }

    private void getWeather(){
        if(Utils.isNetworkAlive(activity)){
            WeatherUtil.getLocationAddressFromBaidu(new CommonListener() {
                @Override
                public Object onExecute(Object o) {
                    handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_CITY_VIEW, o));
                    if(o != null){
                        UmengUtil.get_location(activity, Constants.GET_LOCATION_URL, o.toString());
                        weatherUrl = WeatherUtil.getWeatherURLByCityName(getActivity(), o.toString());
                        if(weatherUrl != null){
                            WeatherUtil.getWeatherInfoByURL(weatherUrl, new WeatherUtil.WeatherInfoListener() {
                                @Override
                                public void onResponse(Weather weatherInfo) {
                                    handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_WEATHER_VIEW, weatherInfo));
                                }
                            });
                        }
                    }
                    return null;
                }
            });
        }else {
            txt_city.setText("网络连接失败");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            String result = data.getExtras().getString("result");
            handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_CITY_VIEW, result));
            WeatherUtil.getWeatherInfoByCity(result, new WeatherUtil.WeatherInfoListener() {
                @Override
                public void onResponse(Weather weatherInfo) {
                    handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_WEATHER_VIEW, weatherInfo));
                }
            });
        }
    }
}
