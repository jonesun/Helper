package jone.helper.model;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by jone.sun on 2015/10/15.
 */
public class BaiduLocationTool {
    private LocationClient mLocClient;
    private BDLocationListener bdLocationListener;
    private void init(Context context){
        // 定位初始化
        mLocClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        //option.setOpenGps(true);// 打开gps
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(1000); //设置发起定位请求的间隔时间为5000ms
        mLocClient.setLocOption(option);
    }

    public void getLocation(Context context, BDLocationListener bdLocationListener){
        this.bdLocationListener = bdLocationListener;
        if(mLocClient == null){
            init(context);
        }
        mLocClient.registerLocationListener(bdLocationListener);
        mLocClient.start();
    }

    public void close(){
        if(mLocClient != null){
            mLocClient.stop();
            mLocClient.unRegisterLocationListener(bdLocationListener);
        }
    }
}
