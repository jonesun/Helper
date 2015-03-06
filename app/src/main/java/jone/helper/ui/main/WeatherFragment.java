package jone.helper.ui.main;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jone.helper.Constants;
import jone.helper.R;
import jone.helper.bean.Weather;
import jone.helper.bean.WeatherData;
import jone.helper.callbacks.CommonListener;
import jone.helper.lib.util.GsonUtils;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

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

    private TextView txt_city, txt_weather;

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
                            txt_city.setText(city);
                        }
                    }else {
                        if(txt_city != null){
                            txt_city.setText("城市获取失败");
                        }
                    }
                    break;
                case WHAT_UPDATE_WEATHER_VIEW:
                    if(msg.obj != null){
                        Weather weather = (Weather) msg.obj;
                        if(txt_weather != null && weather != null){
                            StringBuffer weatherStringBuffer = new StringBuffer();
                            weatherStringBuffer.append("当前城市: " + weather.getCurrentCity()).append("\n");
                            List<WeatherData> weatherDatas = weather.getWeather_data();
                            if(weatherDatas != null && weatherDatas.size() > 0){
                                WeatherData weatherData = weatherDatas.get(0);
                                weatherStringBuffer.append("温度: " + weatherData.getTemperature() + "\n"
                                        + "天气: " + weatherData.getWeather() + "(" + weatherData.getWind() + ")\n"
                                        + "发布时间: " + weatherData.getDate());
                            }
                            txt_weather.setText(weatherStringBuffer);
                        }
                    }else {
                        if(txt_weather != null){
                            txt_weather.setText("天气获取失败");
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_city = (TextView) view.findViewById(R.id.txt_city);
        txt_weather = (TextView) view.findViewById(R.id.txt_weather);
    }

    @Override
    public void onResume() {
        super.onResume();
        WeatherUtil.getLocationAddressFromBaidu(new CommonListener() {
            @Override
            public Object onExecute(Object o) {
                handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_CITY_VIEW, o));
                if(o != null){
                    UmengUtil.get_location(getActivity(), Constants.GET_LOCATION_URL, o.toString());
                    String weatherUrl = WeatherUtil.getWeatherURLByCityName(getActivity(), o.toString());
                    Log.e("sss", "weatherUrl: " + weatherUrl);
                    if(weatherUrl != null){
                        WeatherUtil.getWeatherInfoByURL(weatherUrl, new WeatherUtil.WeatherInfoListener() {
                            @Override
                            public void onResponse(Weather weatherInfo) {
                                handler.sendMessage(handler.obtainMessage(WHAT_UPDATE_WEATHER_VIEW, weatherInfo));
                                if(weatherInfo != null){
                                    UmengUtil.get_weather(getActivity(), Constants.GET_LOCATION_URL, GsonUtils.toJson(weatherInfo));
                                }
                            }
                        });
                    }
                }
                return null;
            }
        });
    }
}
