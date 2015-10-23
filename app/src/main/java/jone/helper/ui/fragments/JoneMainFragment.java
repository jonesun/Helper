package jone.helper.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jone.helper.R;
import jone.helper.model.BaiduLocationTool;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.lib.util.BitmapUtil;
import jone.helper.lib.util.SystemUtil;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

public class JoneMainFragment extends Fragment implements TextToSpeech.OnInitListener{
    private static final String TAG = JoneMainFragment.class.getSimpleName();
    private TextToSpeech textToSpeech;
    private TextSwitcher textSwitcherNews;

    private TextView txtLocation, txtWeather;
    private ImageView imWeatherIcon;
    private Bitmap weatherBitmap = null;

    private Runnable showNewsRunnable;
    private int currentNewsIndex = 0;
    private String news[] = new String[]{
            "你若想得到这世界最好的东西，先提供这世界最好的你。",
            "如果你没有梦想，那么你只能为别人的梦想打工。",
            "你的父母仍在为你打拼，这就是你今天坚强的理由。",
            "什么是坚持?就是每天告诉自己，再坚持一天。",
            "祝新的一年里马到成功！",
    };

    private LinearLayout wp_ad_min_layout, wp_ad_layout;

    private BroadcastReceiver networkChangeBroadcastReceiver;

    private Calendar calendar;
    private FestivalUtil festivalUtil;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://更新天气
                    updateWeatherUI((Weather) msg.obj);
                    break;
            }
        }
    };

    private BaiduLocationTool baiduLocationTool;
    private static JoneMainFragment instance = null;
    public static JoneMainFragment getInstance(){
        if(instance == null){
            instance = new JoneMainFragment();
        }
        return instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentNewsIndex = (int)(Math.random()*news.length);
        textToSpeech = new TextToSpeech(getActivity(), this);
        baiduLocationTool = new BaiduLocationTool();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jone_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        bindBroadcast();
    }
    private void initViews(final View rootView){
        txtLocation = (TextView) rootView.findViewById(R.id.txtLocation);
        txtWeather = (TextView) rootView.findViewById(R.id.txtWeather);
        imWeatherIcon = (ImageView) rootView.findViewById(R.id.imWeatherIcon);
        imWeatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sayMsg();
            }
        });

        wp_ad_min_layout = (LinearLayout) rootView.findViewById(R.id.wp_ad_min_layout);
        wp_ad_layout = (LinearLayout) rootView.findViewById(R.id.wp_ad_layout);

        textSwitcherNews = (TextSwitcher) rootView.findViewById(R.id.textSwitcherNews);
        initTextSwitcherNews();

        handler.post(new Runnable() {
            @Override
            public void run() {
                calendar = Calendar.getInstance();
                festivalUtil = new FestivalUtil(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
                showDate(rootView); //显示时间
                showFestival(rootView); //显示节日
            }
        });
    }

    private void initTextSwitcherNews(){
        textSwitcherNews.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(getActivity());
                tv.setTextColor(getResources().getColor(android.R.color.black));
                tv.setTextSize(16);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        });

        // 设置淡入淡出的动画效果
        Animation in = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_out_right);
        textSwitcherNews.setInAnimation(in);
        textSwitcherNews.setOutAnimation(out);

        showNewsRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentNewsIndex > news.length - 1) {
                    currentNewsIndex = 0;
                }
                textSwitcherNews.setText(news[currentNewsIndex]);
                currentNewsIndex++;
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(showNewsRunnable, 500);
    }

    private void showDate(View rootView){
        TextView txt_date = (TextView) rootView.findViewById(R.id.txt_date);
        txt_date.setText(calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + "周" + FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1)  +" 农历:" + festivalUtil.getChineseDate());
    }

    private void showFestival(View rootView){
        TextView txt_festival = (TextView) rootView.findViewById(R.id.txt_festival);
        ArrayList<String> fest = festivalUtil.getFestVals();
        StringBuffer festival = new StringBuffer();
//            String festival = "";
        if(fest.size() > 0){
            for(String str:fest){
                festival.append(str + "(" + FestivalUtil.getPinYin(str).trim() + ")" +" ");
//                    festival += str + " ";
                System.out.println(str + "(" + FestivalUtil.getPinYin(str, "_").trim() + ")");
            }
            txt_festival.setText("今天是: " + festival);
        }else {
            txt_festival.setText("今天没有节日");
        }
    }

    private void sayMsg(){
        if(textToSpeech != null){
            textToSpeech.speak("hello I'm Jone.",
                    TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                    null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak("Today is " + WeatherUtil.getEnWeek(),
                            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                            null);
                }
            }, 2 * 1000);

        }
    }

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络状态变化
        networkChangeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        System.out.println("网络状态变化");
                        setWeatherInfo();
                        break;
                }
            }
        };
        getActivity().registerReceiver(networkChangeBroadcastReceiver, intentFilter);
    }

    private void unBindBroadcast(){
        if(networkChangeBroadcastReceiver != null){
            getActivity().unregisterReceiver(networkChangeBroadcastReceiver);
        }
    }

    private void setWeatherInfo(){
        if(SystemUtil.isNetworkAlive(getActivity())){
            imWeatherIcon.setVisibility(View.INVISIBLE);
            txtLocation.setText("loading...");
            txtWeather.setText("");
            baiduLocationTool.getLocation(getContext(), new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null) {
                        String city = bdLocation.getCity();
                        if (!TextUtils.isEmpty(city)) {
                            Log.e(TAG, "city: " + city);
                            UmengUtil.get_location(getContext(), "baiduLocation", city);
                            WeatherUtil.getWeatherInfoByCity(city.replace("市", ""), new WeatherUtil.WeatherInfoListener() {
                                @Override
                                public void onResponse(Weather weatherInfo) {
                                    if (weatherInfo != null) {
                                        System.out.println("weatherInfo: " + weatherInfo.getCurrentCity());
                                        Message message = new Message();
                                        message.what = 0;
                                        message.obj = weatherInfo;
                                        handler.sendMessage(message);
                                    } else {
                                        txtLocation.setText("天气获取失败");
                                        txtWeather.setText("");
                                    }
                                }
                            });
                        }
                    }
                    if(baiduLocationTool != null){
                        baiduLocationTool.close();
                    }
                }
            });
        }else {
            txtLocation.setText("当前城市: 未知");
            txtWeather.setText("天气获取失败");
        }
    }

    private void updateWeatherUI(Weather weatherInfo){
        imWeatherIcon.setVisibility(View.VISIBLE);
        if(weatherInfo != null){
            txtLocation.setText("当前城市: " + weatherInfo.getCurrentCity());
            List<WeatherData> weatherDatas = weatherInfo.getWeather_data();
            if(weatherDatas != null && weatherDatas.size() > 0){
                WeatherData weatherData = weatherDatas.get(0);
                txtWeather.setText("温度: " + weatherData.getTemperature() + "\n"
                        + "天气: " + weatherData.getWeather() + "(" + weatherData.getWind() + ")\n"
                        + "时间: " + weatherData.getDate());
                Bitmap weatherBitmap = BitmapFactory.decodeResource(getResources(), WeatherUtil.getWeatherIconByWeather(weatherData.getWeather()));
                weatherBitmap = BitmapUtil.createReflectedImage(weatherBitmap);
                imWeatherIcon.setImageBitmap(weatherBitmap);
            }
        }else {
            txtLocation.setText("当前城市: 未知");
            txtWeather.setText("天气获取失败");
            imWeatherIcon.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(showNewsRunnable != null){
            handler.removeCallbacks(showNewsRunnable);
        }
        BitmapUtil.recycleBitmap(weatherBitmap);
        unBindBroadcast();
        if(baiduLocationTool != null){
            baiduLocationTool.close();
        }
    }

    @Override
    public void onInit(int status) {
// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = textToSpeech.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                Log.d(TAG, "Language is available.");
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                // Greet the user.
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
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
