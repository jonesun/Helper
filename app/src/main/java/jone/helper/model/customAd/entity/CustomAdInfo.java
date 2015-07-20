package jone.helper.model.customAd.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/7/20.
 */
public class CustomAdInfo implements Serializable {
    private String adId; //广告id
    private String adName; //广告标题
    private String adText; //广告语文字
    private Bitmap adIcon; //广告图标(48*48像素)
    private int adPoint; //广告积分
    private String description; //应用描述
    private String version; //程序版本
    private String filesize; //安装包大小
    private String provider; //应用提供商
    private String[] imageUrls; //应用截图的URL数组，每个应用2张截图
    private String adPackage; //广告应用包名
    private String action; //用户存储"安装"或"注册"的字段

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdText() {
        return adText;
    }

    public void setAdText(String adText) {
        this.adText = adText;
    }

    public Bitmap getAdIcon() {
        return adIcon;
    }

    public void setAdIcon(Bitmap adIcon) {
        this.adIcon = adIcon;
    }

    public int getAdPoint() {
        return adPoint;
    }

    public void setAdPoint(int adPoint) {
        this.adPoint = adPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getAdPackage() {
        return adPackage;
    }

    public void setAdPackage(String adPackage) {
        this.adPackage = adPackage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
