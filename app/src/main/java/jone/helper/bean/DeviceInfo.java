package jone.helper.bean;


import java.io.Serializable;

/**
 * Created by jone.sun on 2015/10/19.
 */
public class DeviceInfo implements Serializable {
    String name;
    String value;

    public DeviceInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}