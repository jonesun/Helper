package jone.helper.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * http://apis.baidu.com/avatardata/historytoday/lookup
 * {
 "total": 27,
 "result": [
 {
 "year": 2002,
 "month": 1,
 "day": 1,
 "title": "欧元正式进入流通",
 "type": 1
 }
 ],
 "error_code": 0,
 "reason": "Succes"
 }
 * Created by jone.sun on 2016/2/14.
 */
public class HistoryToday implements Parcelable {
    private int year;
    private int month;
    private int day;
    private String title;
    private int type; //数据类型，1：国内国际大事件，2：民间事件包含部分国家大事件


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeString(this.title);
        dest.writeInt(this.type);
    }

    public HistoryToday() {
    }

    protected HistoryToday(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<HistoryToday> CREATOR = new Parcelable.Creator<HistoryToday>() {
        public HistoryToday createFromParcel(Parcel source) {
            return new HistoryToday(source);
        }

        public HistoryToday[] newArray(int size) {
            return new HistoryToday[size];
        }
    };

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
