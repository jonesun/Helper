package jone.helper.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import jone.helper.App;
import jone.helper.Constants;
import jone.helper.R;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.callbacks.CommonListener;
import jone.net.NetResponseCallback;

/**
 * @author jone_admin on 2014/4/28.
 */
public class WeatherUtil {

    /**
     * 从百度获取当前所在城市
     * @param locationListener
     */
    public static void getLocationAddressFromBaidu(final CommonListener locationListener){
        NetworkRequest request = new NetworkRequest.Builder()
                .get()
                .url(Constants.GET_LOCATION_URL)
                .setUsingCacheWithNoNetwork(true).build();
        App.getVolleyNetworkOperator().request(request, JSONObject.class, new ResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response, boolean fromCache) {
                try {
                    JSONObject locationContent = response.getJSONObject(Constants.LOCATION_CONTENT);
                    String address = locationContent.isNull(Constants.LOCATION_ADDRESS) ? "北京" : locationContent.getString(Constants.LOCATION_ADDRESS);
                    Log.e("sss", "getLocationAddressFromBaidu:" + address);
                    String city = null;
                    if(address.contains("省") && address.contains("市")){
                        city = address.substring(address.indexOf("省") + 1, address.indexOf("市"));
                        System.out.println("getLocationAddressFromBaidu:" + city);
                    }

                    locationListener.onExecute(city);
                } catch (JSONException e) {
//                            e.printStackTrace();
                    locationListener.onExecute(null);
                    Log.e("baiduapi", "获取当前位置失败 ", e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                locationListener.onExecute(null);
                Log.d("baiduapi", "获取当前位置失败，请检查网络连接: " + e.getMessage());
            }
        });
    }

    /**
     * 从天气网中获取指定城市的网址
     * @param context
     * @param cityName
     * @return
     */
    public static String getWeatherURLByCityName(Context context, String cityName){
        //String url = "http://www.weather.com.cn/data/cityinfo/101190401.html"; //X默认苏州天气(101190401);
        //http://api.map.baidu.com/telematics/v3/weather?location=苏州&output=json&ak=oRPA5xAE6pkjYghCDXDGGOiO
        String url = null;
        if(cityName != null){
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String packageNames = info.packageName;
                String DB_PATH = "/data/data/" + packageNames + "/databases/";
                String DB_NAME = "weather_city.db";
                if ((new File(DB_PATH + DB_NAME)).exists() == false) {
                    File f = new File(DB_PATH);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    try {
                        InputStream is = context.getAssets().open(DB_NAME);
                        OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        os.flush();
                        os.close();
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
                Cursor cursor = database.rawQuery("SELECT * FROM weathercity WHERE name=?", new String[]{cityName});
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String code = cursor.getString(cursor.getColumnIndex("code"));
                    // 看输出的信息是否正确
                    System.out.println("weathercity name: " + name + ", code: " + code);
                    try {
                        name = URLEncoder.encode(name, "utf-8");
                        url = "http://api.map.baidu.com/telematics/v3/weather?location=" + name + "&output=json&ak=" + Constants.BAIDU_AK;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // url = "http://www.weather.com.cn/data/cityinfo/" + code + ".html";
                    //url = "http://api.map.baidu.com/telematics/v3/weather?location=%E8%8B%8F%E5%B7%9E&output=json&ak=oRPA5xAE6pkjYghCDXDGGOiO";
                }
                cursor.close();
                database.close();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    /**
     * 通过指定URL从天气网获取天气信息
     * @param weatherInfoListener
     */
    public static void getWeatherInfoByURL(final String url, final WeatherInfoListener weatherInfoListener){
        if(url != null){
            NetworkRequest request = new NetworkRequest.Builder()
                    .get()
                    .url(url)
                    .setUsingCacheWithNoNetwork(true).build();
            App.getVolleyNetworkOperator().request(request, JSONObject.class, new ResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response, boolean fromCache) {
                    Weather weather = null;
                    try {
                        if (response.has("status")) {
                            String status = response.getString("status");
                            if (status.equals("success") && response.has("results")) {
                                JSONArray results = response.getJSONArray("results");
                                if (results != null && results.length() > 0) {
                                    String weatherStr = results.get(0).toString();
                                    weather = new Gson().fromJson(weatherStr, Weather.class);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("WeatherUtil", "getWeatherInfoByURL", e);
                    } finally {
                        weatherInfoListener.onResponse(weather);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("getWeatherInfo", "获取天气信息失败，请检查网络连接: " + e.getMessage());
                    weatherInfoListener.onResponse(null);
                }
            });
        }else {
            weatherInfoListener.onResponse(null);
        }

    }

    /**
     * 获取指定城市的天气信息
     * @param city
     * @param weatherInfoListener
     */
    public static void getWeatherInfoByCity(String city, final WeatherInfoListener weatherInfoListener){
        getWeatherInfoByURL(getWeatherURLByCityName(App.getInstance(), city), weatherInfoListener);
    }

    /**
     * 获取当前城市的天气信息
     * @param weatherInfoListener
     */
    public static void getLocationCityWeatherInfo(final WeatherInfoListener weatherInfoListener){
        getLocationAddressFromBaidu(new CommonListener() {
            @Override
            public Object onExecute(Object o) {
                String url = getWeatherURLByCityName(App.getInstance(), (String) o);
                System.out.println("url: " + url);
                getWeatherInfoByURL(url, weatherInfoListener);
                return null;
            }
        });
    }

    public interface WeatherInfoListener{
        void onResponse(Weather weatherInfo);
    }

    public static int getWeatherIconByWeather(String weather){
//        晴0、多云1、阴2、阵雨3、雷阵雨4、雷阵雨伴有冰雹5、雨夹雪6、小雨7、
//        中雨8、大雨9、暴雨10、大暴雨11、特大暴雨12、阵雪13、小雪14、中雪15、
//        大雪16、暴雪17、雾18、冻雨19、沙尘暴20、小雨-中雨21、中雨-大雨22、大雨-暴雨23、
//        暴雨-大暴雨24、大暴雨-特大暴雨25、小雪-中雪26、中雪-大雪27、大雪-暴雪28、浮尘29、扬沙30、强沙尘暴31、
//        霾32
        int weatherIcon;
        if(weather == null){
            weatherIcon = R.drawable.ic_weather_sunny;
        }else {
            Calendar calendar = Calendar.getInstance();
            int hour= calendar.get(Calendar.HOUR_OF_DAY);
            if(hour > 17){ //晚间
                weatherIcon = R.drawable.weather_night;
            }else {
                if(weather == null || weather.equals("晴")){
                    weatherIcon = R.drawable.ic_weather_sunny;
                }else if(weather.equals("多云") || weather.equals("多云转晴")){
                    weatherIcon = R.drawable.weather_cloudy;
                }else if(weather.equals("阴")){
                    weatherIcon = R.drawable.weather_overcast;
                }else if(weather.equals("阵雨")){
                    weatherIcon = R.drawable.weather_shower;
                }else if(weather.equals("霾")){
                    weatherIcon = R.drawable.weather_haze;
                }else if(weather.equals("雨夹雪")){
                    weatherIcon = R.drawable.weather_sleet;
                }else if(weather.contains("雷")){
                    weatherIcon = R.drawable.weather_thunder;
                }else if(weather.contains("雨")){
                    weatherIcon = R.drawable.weather_rain;
                }else if(weather.contains("雪")){
                    weatherIcon = R.drawable.weather_snow;
                }else {
                    weatherIcon = R.drawable.weather_fog;
                }
            }
        }
        return weatherIcon;
    }

    public static int getIconResIdByWeather(String weather){
//        晴0、多云1、阴2、阵雨3、雷阵雨4、雷阵雨伴有冰雹5、雨夹雪6、小雨7、
//        中雨8、大雨9、暴雨10、大暴雨11、特大暴雨12、阵雪13、小雪14、中雪15、
//        大雪16、暴雪17、雾18、冻雨19、沙尘暴20、小雨-中雨21、中雨-大雨22、大雨-暴雨23、
//        暴雨-大暴雨24、大暴雨-特大暴雨25、小雪-中雪26、中雪-大雪27、大雪-暴雪28、浮尘29、扬沙30、强沙尘暴31、
//        霾32
        int weatherIcon;
        if(weather == null){
            weatherIcon = R.drawable.ic_weather_sunny;
        }else {
            Calendar calendar = Calendar.getInstance();
            int hour= calendar.get(Calendar.HOUR_OF_DAY);
            if(hour > 17){ //晚间
                weatherIcon = R.drawable.weather_night;
            }else {
                if(weather == null || weather.contains("晴")){
                    weatherIcon = R.drawable.ic_weather_sunny;
                }else if(weather.contains("多云")){
                    weatherIcon = R.drawable.weather_cloudy;
                }else if(weather.contains("阴")){
                    weatherIcon = R.drawable.weather_overcast;
                }else if(weather.contains("阵雨")){
                    weatherIcon = R.drawable.weather_shower;
                }else if(weather.contains("霾")){
                    weatherIcon = R.drawable.weather_haze;
                }else if(weather.contains("雨夹雪")){
                    weatherIcon = R.drawable.weather_sleet;
                }else if(weather.contains("雷")){
                    weatherIcon = R.drawable.weather_thunder;
                }else if(weather.contains("雨")){
                    weatherIcon = R.drawable.weather_rain;
                }else if(weather.contains("雪")){
                    weatherIcon = R.drawable.weather_snow;
                }else {
                    weatherIcon = R.drawable.weather_fog;
                }
            }
        }
        return weatherIcon;
    }

    public static boolean isUpdateCalendar(Calendar calendar){
        //每天0:00更新日历
        if((calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) || (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 1)){
            System.out.println("更新日历");
            return true;
        }
        return false;
    }

    public static boolean isUpdateWeather(Context context, Calendar calendar){
        //每天0:10、8:10、16:10更新天气
        int hour;
        if(DateFormat.is24HourFormat(context)){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        }else {
            hour = calendar.get(Calendar.HOUR);
        }
        if(((hour % 8) == 0) && (calendar.get(Calendar.MINUTE) == 10)){
            System.out.println("更新天气: " + hour);
            return true;
        }
        return false;
    }

    public static String getEnWeek(){
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        switch (week){
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "";
        }
    }

    public static String getPm25String(String pm25){
        try{
            int p = Integer.parseInt(pm25);
            if(p >= 0 && p <= 35){
                return "优";
            }else if(p > 35 && p <= 75){
                return "良";
            }else if( p > 75 && p <= 115){
                return "轻度污染";
            }else if( p > 115 && p <= 150){
                return "中度污染";
            }else if( p > 150 && p <= 250){
                return "重度污染";
            }else if( p > 250){
                return "严重污染";
            }else {
                return "未知";
            }
        }catch (Exception e){
            return "未知";
        }

    }
}
