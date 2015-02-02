package jone.helper.lib.processDataOperator;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by jone.sun on 2015/2/2.
 */
public interface ProcessDataOperator {
    <T>void putValue(String key, T value);

    void batchPutValue(ContentValues contentValues);

    int getValueByKey(String key, int defaultValue);

    float getValueByKey(String key, float defaultValue);

    long getValueByKey(String key, long defaultValue);

    boolean getValueByKey(String key, boolean defaultValue);

    String getValueByKey(String key, String defaultValue);

    boolean delValueByKey(String key);
}
