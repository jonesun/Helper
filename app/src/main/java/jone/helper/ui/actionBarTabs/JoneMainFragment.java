package jone.helper.ui.actionBarTabs;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.R;
import jone.helper.adapter.ToolsAdapter;
import jone.helper.asyncTaskLoader.CustomListAsyncTaskLoader;
import jone.helper.bean.WeatherInfo;
import jone.helper.callbacks.CommonListener;
import jone.helper.lib.util.SystemUtil;
import jone.helper.logic.ToolsLogic;
import jone.helper.util.FestivalUtil;
import jone.helper.util.WeatherUtil;

public class JoneMainFragment extends Fragment {
    private static final String TAG = JoneMainFragment.class.getSimpleName();
    public static final int loaderId = 001001;
    private TextSwitcher textSwitcherNews;

    private GridView gridViewCenter;
    private ToolsAdapter adapter;
    private ToolsLogic toolsLogic;
    private LoaderManager loaderManager;

    private TextView txtLocation, txtWeather;
    private ImageView imWeatherIcon;

    private Runnable showNewsRunnable;
    private int currentNewsIndex = 0;
    private String news[] = new String[]{
            "你若想得到这世界最好的东西，先提供这世界最好的你。",
            "如果你没有梦想，那么你只能为别人的梦想打工。",
            "你的父母仍在为你打拼，这就是你今天坚强的理由。",
            "什么是坚持?就是每天告诉自己，再坚持一天。",
            "祝新的一年里马到成功！",
    };

    private BroadcastReceiver networkChangeBroadcastReceiver;

    private Calendar calendar;
    private FestivalUtil festivalUtil;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://更新天气
                    updateWeatherUI((WeatherInfo) msg.obj);
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolsLogic = new ToolsLogic(getActivity());
        loaderManager = getLoaderManager();
        currentNewsIndex = (int)(Math.random()*news.length);
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
                setWeatherInfo();
            }
        });

        textSwitcherNews = (TextSwitcher) rootView.findViewById(R.id.textSwitcherNews);
        initTextSwitcherNews();
        gridViewCenter = (GridView) rootView.findViewById(R.id.gridViewCenter);
        adapter = new ToolsAdapter(getActivity());
        gridViewCenter.setAdapter(adapter);
        loaderManager.initLoader(loaderId, null, callbacks);

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
                tv.setTextSize(16);
                //tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
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

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络状态变化
        networkChangeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("网络状态变化");
                setWeatherInfo();
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
            WeatherUtil.getLocationCityWeatherInfo(new WeatherUtil.WeatherInfoListener() {
                @Override
                public void onResponse(WeatherInfo weatherInfo) {
                    System.out.println("weatherInfo: " + weatherInfo.getCity());
                    Message message = new Message();
                    message.what = 0;
                    message.obj = weatherInfo;
                    handler.sendMessage(message);
                }
            });
        }else {
            txtLocation.setText("当前城市: 未知");
            txtWeather.setText("天气获取失败");
        }
    }

    private void updateWeatherUI(WeatherInfo weatherInfo){
        imWeatherIcon.setVisibility(View.VISIBLE);
        if(weatherInfo != null){
            txtLocation.setText("当前城市: " + weatherInfo.getCity());
            txtWeather.setText("温度: " + weatherInfo.getTemp1() + "-" + weatherInfo.getTemp2() + "\n"
                    + "天气: " + weatherInfo.getWeather() + "\n"
                    + "发布时间: " + weatherInfo.getPtime());
            imWeatherIcon.setImageResource(WeatherUtil.getWeatherIconByWeather(weatherInfo.getWeather()));
        }else {
            txtLocation.setText("当前城市: 未知");
            txtWeather.setText("天气获取失败");
            imWeatherIcon.setVisibility(View.INVISIBLE);
        }
    }

    private LoaderManager.LoaderCallbacks<List> callbacks = new LoaderManager.LoaderCallbacks<List>() {
        @Override
        public Loader<List> onCreateLoader(int i, Bundle bundle) {
            return new CustomListAsyncTaskLoader(getActivity(), new CommonListener() {
                @Override
                public Object onExecute(Object o) {
                    return toolsLogic.getToolBeans();
                }
            });
        }

        @Override
        public void onLoadFinished(Loader<List> listLoader, List list) {
            adapter.setData(list);
        }

        @Override
        public void onLoaderReset(Loader<List> listLoader) {
            adapter.clear();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loaderManager.getLoader(loaderId) != null){
            loaderManager.destroyLoader(loaderId);
        }
        if(showNewsRunnable != null){
            handler.removeCallbacks(showNewsRunnable);
        }
        unBindBroadcast();
    }
}
