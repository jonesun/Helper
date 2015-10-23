package jone.helper.bean;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/10/22.
 */
public class DateInfo implements Serializable {
    private String dataStr;
    private String[] calendars = new String[7];
    private int todayIndex;

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public String[] getCalendars() {
        return calendars;
    }

    public void setCalendars(String[] calendars) {
        this.calendars = calendars;
    }

    public int getTodayIndex() {
        return todayIndex;
    }

    public void setTodayIndex(int todayIndex) {
        this.todayIndex = todayIndex;
    }
}
