package jone.helper.model.bing;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPicture implements Parcelable {
    private String url;
    private String copyright;
    private ArrayList<BingPictureHs> hs;
    private ArrayList<BingPictureMsg> msg;

    public BingPicture(){}



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public ArrayList<BingPictureHs> getHs() {
        return hs;
    }

    public void setHs(ArrayList<BingPictureHs> hs) {
        this.hs = hs;
    }

    public ArrayList<BingPictureMsg> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<BingPictureMsg> msg) {
        this.msg = msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.copyright);
        dest.writeList(this.hs);
        dest.writeList(this.msg);
    }

    protected BingPicture(Parcel in) {
        this.url = in.readString();
        this.copyright = in.readString();
        this.hs = in.readArrayList(BingPictureHs.class.getClassLoader());//class loader 必须要指明
        this.msg = in.readArrayList(BingPictureMsg.class.getClassLoader());//class loader 必须要指明
    }

    public static final Parcelable.Creator<BingPicture> CREATOR = new Parcelable.Creator<BingPicture>() {
        public BingPicture createFromParcel(Parcel source) {
            return new BingPicture(source);
        }

        public BingPicture[] newArray(int size) {
            return new BingPicture[size];
        }
    };
}
