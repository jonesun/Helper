package jone.helper.bean;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/11/12.
 */
public class HomeMainCalendarBean implements Serializable{
    private String label;
    private String calendar;
    private boolean isToday;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }
}
