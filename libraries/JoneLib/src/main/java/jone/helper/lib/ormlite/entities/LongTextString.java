package jone.helper.lib.ormlite.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 大文本字符串
 * @author jone.sun on 2015/3/24.
 */
@DatabaseTable
public class LongTextString implements Serializable{
    @DatabaseField(id = true)
    private String key;

    @DatabaseField(columnDefinition = "TEXT")
    private String value;

    public LongTextString(){}

    public LongTextString(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

