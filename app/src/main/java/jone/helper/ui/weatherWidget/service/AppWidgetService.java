package jone.helper.ui.weatherWidget.service;

import android.app.AlertDialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.Weather;
import jone.helper.bean.WeatherData;
import jone.helper.lib.util.Utils;
import jone.helper.ui.weatherWidget.ui.WeatherWidget;
import jone.helper.util.Lunar;
import jone.helper.util.WeatherUtil;

/**
 * Created by jone_admin on 13-12-30.
 */
public class AppWidgetService extends Service{
    private final static String TAG = AppWidgetService.class.getName();
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateTime(System.currentTimeMillis());
        updateCalendar(Calendar.getInstance());
        App.getInstance().getHandler().post(runnableGetWeather);
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long currentTime = System.currentTimeMillis() + 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(currentTime));
            updateTime(currentTime);
            if(WeatherUtil.isUpdateWeather(AppWidgetService.this, calendar)){
                //更新天气
                App.getInstance().getHandler().post(runnableGetWeather);//更新天气
            }
            if(WeatherUtil.isUpdateCalendar(calendar)){
                updateCalendar(calendar);
            }
//            showBox(AppWidgetService.this);
        }
    };

    private void showBox(final Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示");
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setMessage("正文");
        dialog.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                // TODO Auto-generated method stub
//                //点击后跳转到某个Activity
//                Intent result = new Intent(context, xxx.class);
//                result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(result);
            }
        });
        AlertDialog mDialog=dialog.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();
    }

    private void updateTime(long currentTime){
        //更新时间
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_widget_weather);
        remoteViews.setTextViewText(R.id.tx_weather_time, geFormatTime(currentTime));
        ComponentName componentName = new ComponentName(this, WeatherWidget.class); //相当于获得所有本程序创建的appwidget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    private void updateCalendar(Calendar calendar){
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.layout_widget_weather);
        String calendarInfo = "周" + Utils.weekNum2string(calendar.get(Calendar.DAY_OF_WEEK)) + " " + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
        remoteViews.setTextViewText(R.id.txt_calendar, calendarInfo);
        try {
            System.out.println(new Lunar().getLunarTime(calendar.getTimeInMillis()));
            remoteViews.setTextViewText(R.id.txt_calendar_lunar, new Lunar().getLunarTime(calendar.getTimeInMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ComponentName componentName = new ComponentName(this, WeatherWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    private String geFormatTime(long currentTime){
        if(DateFormat.is24HourFormat(AppWidgetService.this)){
            return Utils.formatDataTime(currentTime, "HH:mm");
        }else {
            return Utils.formatDataTime(currentTime, "hh:mm");
        }
    }

    private Runnable runnableGetWeather = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "getWeatherRunnable");
            if(Utils.isNetworkAlive(AppWidgetService.this)){
                WeatherUtil.getLocationCityWeatherInfo(new WeatherUtil.WeatherInfoListener() {
                    @Override
                    public void onResponse(Weather weatherInfo) {
                        if(weatherInfo != null){
                            updateWeather(weatherInfo);
                        }else {
                            Toast.makeText(AppWidgetService.this, "天气更新失败, 请检查网络", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Toast.makeText(AppWidgetService.this, "天气更新失败, 请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updateWeather(Weather weatherInfo){
        if(weatherInfo != null){
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.layout_widget_weather);
            remoteViews.setTextViewText(R.id.tx_weather_city, weatherInfo.getCurrentCity());
            List<WeatherData> weatherDatas = weatherInfo.getWeather_data();
            if(weatherDatas != null && weatherDatas.size() > 0){
                WeatherData weatherData = weatherDatas.get(0);
                remoteViews.setTextViewText(R.id.tx_weather_template, weatherData.getTemperature());
                remoteViews.setTextViewText(R.id.tx_weather_weather, weatherData.getWeather());
                remoteViews.setImageViewResource(R.id.im_weather_icon, WeatherUtil.getWeatherIconByWeather(weatherData.getWeather()));
            }
            ComponentName componentName = new ComponentName(this, WeatherWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
