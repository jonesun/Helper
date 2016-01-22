package jone.helper.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片对象
 * Created by jone.sun on 2016/1/22.
 */
public class PictureBean implements Parcelable {
    private String title;
    private String detail;
    private String uri;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.detail);
        dest.writeString(this.uri);
    }

    public PictureBean() {
    }

    public PictureBean(String title, String detail, String uri) {
        this.title = title;
        this.detail = detail;
        this.uri = uri;
    }

    protected PictureBean(Parcel in) {
        this.title = in.readString();
        this.detail = in.readString();
        this.uri = in.readString();
    }

    public static final Parcelable.Creator<PictureBean> CREATOR = new Parcelable.Creator<PictureBean>() {
        public PictureBean createFromParcel(Parcel source) {
            return new PictureBean(source);
        }

        public PictureBean[] newArray(int size) {
            return new PictureBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
