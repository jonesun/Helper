package jone.helper.ui.weatherWidget.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import jone.helper.R;
import jone.helper.ui.weatherWidget.service.AppWidgetService;


/**
 * Created by jone_admin on 14-1-3.
 */
public class WeatherWidget extends AppWidgetProvider{
    private final static String TAG = WeatherWidget.class.getName();
    public final static String UPDATE_WEATHER = "jone.intent.action.update.weather";

    public WeatherWidget() {
        super();
    }
    /**
     * s接受广播事件
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: " + intent.getAction());
        if(intent.getAction().equals(UPDATE_WEATHER) || intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
            Intent i = new Intent(context, AppWidgetService.class);
            i.setAction(UPDATE_WEATHER);
            context.startService(i);
        }
        super.onReceive(context, intent);
    }

    /**
     * s到达指定的更新时间或者当用户向桌面添加AppWidget时被调用
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG, "onUpdate");
        Intent intent = new Intent();
        intent.setAction(UPDATE_WEATHER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget_weather);
        remoteViews.setOnClickPendingIntent(R.id.im_weather_icon, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * s删除一个AppWidget时调用
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * sAppWidget的实例第一次被创建时调用
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        Log.e(TAG, "onEnabled");
        //context.startService(new Intent(context, AppWidgetService.class));
        super.onEnabled(context);
    }

    /**
     * s最后一个appWidget被删除事调用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        Log.e(TAG, "onDisabled");
        Intent i = new Intent(context, AppWidgetService.class);
        i.setAction(UPDATE_WEATHER);
        context.stopService(i);
        super.onDisabled(context);
    }
}
