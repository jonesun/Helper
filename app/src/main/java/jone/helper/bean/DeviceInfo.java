package jone.helper.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import jone.helper.BR;

/**
 * Created by jone.sun on 2015/10/19.
 */
public class DeviceInfo extends BaseObservable {
    String name;
    String value;

    public DeviceInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }


    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        notifyPropertyChanged(BR.value);
    }
}