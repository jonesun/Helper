package jone.helper.model.bing;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPictureHs implements Parcelable {
    private String desc;
    private String link;
    private String query;
    private int locx;
    private int locy;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLocx() {
        return locx;
    }

    public void setLocx(int locx) {
        this.locx = locx;
    }

    public int getLocy() {
        return locy;
    }

    public void setLocy(int locy) {
        this.locy = locy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.link);
        dest.writeString(this.query);
        dest.writeInt(this.locx);
        dest.writeInt(this.locy);
    }

    public BingPictureHs() {
    }

    protected BingPictureHs(Parcel in) {
        this.desc = in.readString();
        this.link = in.readString();
        this.query = in.readString();
        this.locx = in.readInt();
        this.locy = in.readInt();
    }

    public static final Parcelable.Creator<BingPictureHs> CREATOR = new Parcelable.Creator<BingPictureHs>() {
        public BingPictureHs createFromParcel(Parcel source) {
            return new BingPictureHs(source);
        }

        public BingPictureHs[] newArray(int size) {
            return new BingPictureHs[size];
        }
    };
}
