package jone.helper.mvp.view.loadData;

import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by jone.sun on 2016/1/13.
 */
public interface LoadDataView<T> {
    public static final int REASON_OF_NO_DATA = -1;
    public static final int REASON_OF_NO_NETWORK = -2;
    public static final int REASON_OF_SERVER_ERROR = -3;
    public static final int REASON_OF_NO_NEXT = -4;

    @IntDef({REASON_OF_NO_DATA, REASON_OF_NO_NETWORK, REASON_OF_SERVER_ERROR, REASON_OF_NO_NEXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Reason {}

    void addDataList(List<T> dataList);
    void showProgress();
    void hideProgress();
    void showLoadFailMsg(@Reason int reason, String message);
}
