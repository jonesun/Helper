package jone.helper.model.customAd.impl;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import jone.helper.AdInfo;
import jone.helper.AppConnect;
import jone.helper.model.customAd.CustomAdInfoModel;
import jone.helper.model.customAd.OnCompleteListener;
import jone.helper.model.customAd.entity.CustomAdInfo;

/**
 * Created by jone.sun on 2015/7/20.
 */
public class JoneWPCustomAdInfoModel implements CustomAdInfoModel {
    @Override
    public void init(Context context){
        AppConnect.getInstance(context).initAdInfo();
    }
    @Override
    public void loadData(Context context, OnCompleteListener onCompleteListener) {
        if(onCompleteListener != null){
            List<CustomAdInfo> customAdInfoList = new ArrayList<>();
            List<AdInfo> adInfoList = AppConnect.getInstance(context).getAdInfoList();
            if(adInfoList != null && adInfoList.size() > 0){
                for(AdInfo adInfo : adInfoList){
                    CustomAdInfo customAdInfo = new CustomAdInfo();
                    customAdInfo.setAdId(adInfo.getAdId());
                    customAdInfo.setAdName(adInfo.getAdName());
                    customAdInfo.setAdText(adInfo.getAdText());
                    customAdInfo.setAdIcon(adInfo.getAdIcon());
                    customAdInfo.setAdPoint(adInfo.getAdPoints());
                    customAdInfo.setDescription(adInfo.getDescription());
                    customAdInfo.setVersion(customAdInfo.getVersion());
                    customAdInfo.setFilesize(customAdInfo.getFilesize());
                    customAdInfo.setProvider(customAdInfo.getProvider());
                    customAdInfo.setImageUrls(customAdInfo.getImageUrls());
                    customAdInfo.setAdPackage(adInfo.getAdPackage());
                    customAdInfo.setAction(adInfo.getAction());
                    customAdInfoList.add(customAdInfo);
                }
            }
            onCompleteListener.onComplete(customAdInfoList);
        }
    }
}
