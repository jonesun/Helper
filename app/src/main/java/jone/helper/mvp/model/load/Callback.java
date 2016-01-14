package jone.helper.mvp.model.load;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jone.sun on 2016/1/12.
 */
public interface Callback<T> {
    public static final int RESULT_CODE_NORMAL = 0;
    public static final int RESULT_CODE_NO_NETWORK = -1;
    public static final int RESULT_CODE_SERVER_ERROR = -2;
    public static final int RESULT_CODE_NO_NEXT = -3;

    @IntDef({RESULT_CODE_NORMAL, RESULT_CODE_NO_NETWORK, RESULT_CODE_SERVER_ERROR, RESULT_CODE_NO_NEXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResultCode {}
    void onComplete(@ResultCode int resultCode, String message, T data);
}
