package jone.helper.model.weather.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import jone.helper.App;
import jone.helper.Constants;
import jone.helper.lib.model.net.NetResponseCallback;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.volley.Method;
import jone.helper.model.weather.OnLocationListener;
import jone.helper.model.weather.OnWeatherListener;
import jone.helper.model.weather.WeatherModel;
import jone.helper.model.weather.entity.Weather;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class BaiduWeatherModel implements WeatherModel {

    @Override
    public void getLocationCity(Context context, final OnLocationListener locationListener) {
        App.getNetJsonOperator().request(Method.GET, Constants.GET_LOCATION_URL, null, new NetResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject locationContent = response.getJSONObject(Constants.LOCATION_CONTENT);
                    String address = locationContent.isNull(Constants.LOCATION_ADDRESS) ? "北京" : locationContent.getString(Constants.LOCATION_ADDRESS);
                    final String city = address.substring(address.indexOf("省") + 1, address.indexOf("市"));
                    System.out.println("getLocationAddressFromBaidu:" + city);
                    if(locationListener != null){
                        locationListener.onSuccess(city);
                    }
                } catch (JSONException e) {
                    if(locationListener != null){
                        locationListener.onError(e.getMessage());
                    }
                    Log.e("baiduapi", "获取当前位置失败 ", e);
                }
            }

            @Override
            public void onFailure(String error) {
                if(locationListener != null){
                    locationListener.onError(error);
                }
                Log.d("baiduapi", "获取当前位置失败，请检查网络连接: " + error);
            }
        });
    }

    @Override
    public void loadData(Context context, String city, final OnWeatherListener listener) {
        String weatherUrl = getWeatherUrlByCityFromBaidu(city);
        App.getNetJsonOperator().request(Method.GET, weatherUrl, null, new NetResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
//                Log.e("loadWeather", "response: " + response.toString());
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
                    if(listener != null){
                        listener.onSuccess(weather);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("getWeatherInfo", "获取天气信息失败，请检查网络连接: " + error);
                if(listener != null){
                    listener.onError(error);
                }
            }
        });
    }

    private String getWeatherUrlByCityFromBaidu(String city){
        String url = null;
        try {
            url = "http://api.map.baidu.com/telematics/v3/weather?location=" + URLEncoder.encode(city, "utf-8") + "&output=json&ak=" + Constants.BAIDU_AK;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取指定城市的天气网址
     * @param context
     * @param city
     * @return
     */
    private String getWeatherURLByCity(Context context, String city){
        //String url = "http://www.weather.com.cn/data/cityinfo/101190401.html"; //X默认苏州天气(101190401);
        //http://api.map.baidu.com/telematics/v3/weather?location=苏州&output=json&ak=oRPA5xAE6pkjYghCDXDGGOiO
        String url = null;
        if(city != null){
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
                Cursor cursor = database.rawQuery("SELECT * FROM weathercity WHERE name=?", new String[]{city});
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
}
