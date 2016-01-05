package jone.helper.model;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import jone.helper.App;
import jone.helper.lib.util.GsonUtils;

/**
 * 高德地图定位工具类
 * Created by jone.sun on 2015/12/17.
 */
public class AMapLocationTool implements AMapLocationListener {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private static AMapLocationTool instance = null;

    public static AMapLocationTool getInstance(){
        if(instance == null){
            instance = new AMapLocationTool();
        }
        return instance;
    }

    private AMapLocationTool(){
        init(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    public void init(AMapLocationClientOption.AMapLocationMode locationMode) {
        //初始化定位
        mLocationClient = new AMapLocationClient(App.getInstance());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        if(locationMode == null){
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving;
        }
        mLocationOption.setLocationMode(locationMode);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            Log.e("AMapLocationTool", "onLocationChanged>>" + GsonUtils.toJson(aMapLocation));
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取经度
                aMapLocation.getLongitude();//获取纬度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getRoad();//街道信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    public void startLocation(AMapLocationClientOption.AMapLocationMode locationMode,
                      AMapLocationListener mLocationListener){
        if(mLocationClient == null){
            init(locationMode);
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //启动定位
        mLocationClient.startLocation();
    }

    public void startLocation(AMapLocationListener mLocationListener){
        startLocation(null, mLocationListener);
    }

    public void stopLocation(){
        if(mLocationClient != null){
            mLocationClient.stopLocation();
        }
    }

    public void destroy(){
        /***销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。***/
        if(mLocationClient != null){
            mLocationClient.onDestroy();//销毁定位客户端。
            mLocationClient = null;
        }
    }
}
