package jone.helper;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jone.sun on 2015/12/2.
 */
public enum BooleanType {
    @SerializedName("0")
    FALSE,
    @SerializedName("1")
    TRUE
}

//public enum BooleanType {
//    FALSE("0"),
//    TRUE("1");
//
//    private String value;
//
//    BooleanType(String value) {
//        this.value = value;
//    }
//
//    public String getValue() {
//        return value;
//    }
//}