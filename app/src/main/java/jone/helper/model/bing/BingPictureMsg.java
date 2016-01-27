package jone.helper.model.bing;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPictureMsg implements Parcelable {
    private String title;
    private String text;
    private String link;

    public BingPictureMsg() {
    }

    protected BingPictureMsg(Parcel in) {
        this.title = in.readString();
        this.text = in.readString();
        this.link = in.readString();
    }

    public static final Parcelable.Creator<BingPictureMsg> CREATOR = new Parcelable.Creator<BingPictureMsg>() {
        public BingPictureMsg createFromParcel(Parcel source) {
            return new BingPictureMsg(source);
        }

        public BingPictureMsg[] newArray(int size) {
            return new BingPictureMsg[size];
        }
    };


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.link);
    }
}
