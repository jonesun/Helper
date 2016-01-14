package jone.helper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {"title":"我更喜欢吃小笼包","thumburl":"http://ww3.sinaimg.cn/large/bd698b0fjw1ez74ug1m8aj20ca0dz74q.jpg","sourceurl":"http://down.laifudao.com/images/tupian/201512818431.jpg","height":"503","width":"442","class":"1","url":"http://www.laifudao.com/tupian/51985.htm"}
 * Created by jone.sun on 2016/1/12.
 */
public class LaiFuDaoPicture implements Parcelable {
    private String title;
    private String thumburl;
    private String sourceurl;
    private String height;
    private String width;
    @SerializedName("class") private String clazz;
    private String url;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.thumburl);
        dest.writeString(this.sourceurl);
        dest.writeString(this.height);
        dest.writeString(this.width);
        dest.writeString(this.clazz);
        dest.writeString(this.url);
    }

    public LaiFuDaoPicture() {
    }

    protected LaiFuDaoPicture(Parcel in) {
        this.title = in.readString();
        this.thumburl = in.readString();
        this.sourceurl = in.readString();
        this.height = in.readString();
        this.width = in.readString();
        this.clazz = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<LaiFuDaoPicture> CREATOR = new Parcelable.Creator<LaiFuDaoPicture>() {
        public LaiFuDaoPicture createFromParcel(Parcel source) {
            return new LaiFuDaoPicture(source);
        }

        public LaiFuDaoPicture[] newArray(int size) {
            return new LaiFuDaoPicture[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumburl() {
        return thumburl;
    }

    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }

    public String getSourceurl() {
        return sourceurl;
    }

    public void setSourceurl(String sourceurl) {
        this.sourceurl = sourceurl;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
