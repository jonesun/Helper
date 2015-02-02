package jone.helper.lib.processDataOperator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import jone.helper.lib.contentProvider.ProcessDataContentProvider;

/**
 * *跨进程读写到SharedPreferences
 * 需在XML的注册ProcessDataContentProvider
 * Created by jone.sun on 2015/2/2.
 */
public class ProcessSpDataOperator implements ProcessDataOperator {
    private static ProcessSpDataOperator instance;
    private Context context;
    private static final Uri URL = ProcessDataContentProvider.SP_URI;

    public ProcessSpDataOperator getInstance(Context context){
        if(instance == null){
            instance = new ProcessSpDataOperator(context);
        }
        return instance;
    }

    private ProcessSpDataOperator(Context context){
        this.context = context;
    }

    @Override
    public <T> void putValue(String key, T value) {
        ContentValues contentValues = new ContentValues();
        if(value instanceof Integer){
            contentValues.put(key, (Integer) value);
        }else if (value instanceof String){
            contentValues.put(key, (String) value);
        }else if(value instanceof Boolean){
            contentValues.put(key, (Boolean) value);
        }else if(value instanceof Long){
            contentValues.put(key, (Long) value);
        }else if(value instanceof Float){
            contentValues.put(key, (Float) value);
        }
        context.getContentResolver().insert(URL, contentValues);
    }

    @Override
    public void batchPutValue(ContentValues contentValues) {
        context.getContentResolver().insert(URL, contentValues);
    }

    @Override
    public int getValueByKey(String key, int defaultValue) {
        int result = defaultValue;
        String resultStr = getResult(context, key, defaultValue);
        if(resultStr != null){
            result = Integer.parseInt(resultStr);
        }
        return result;
    }

    @Override
    public float getValueByKey(String key, float defaultValue) {
        float result = defaultValue;
        String resultStr = getResult(context, key, defaultValue);
        if(resultStr != null){
            result = Float.parseFloat(resultStr);
        }
        return result;
    }

    @Override
    public long getValueByKey(String key, long defaultValue) {
        long result = defaultValue;
        String resultStr = getResult(context, key, defaultValue);
        if(resultStr != null){
            result = Long.parseLong(resultStr);
        }
        return result;
    }

    @Override
    public boolean getValueByKey(String key, boolean defaultValue) {
        boolean result = defaultValue;
        String resultStr = getResult(context, key, defaultValue);
        if(resultStr != null){
            result = Boolean.parseBoolean(resultStr);
        }
        return result;
    }

    @Override
    public String getValueByKey(String key, String defaultValue) {
        String result = defaultValue;
        String resultStr = getResult(context, key, defaultValue);
        if(resultStr != null){
            result = resultStr;
        }
        return result;
    }

    @Override
    public boolean delValueByKey(String key) {
        return false;
    }

    private static <T>String getResult(Context context, String key, T defaultValue){
        String result = null;
        String strings[] = null;
        if(defaultValue != null){
            strings = new String[]{defaultValue.getClass().getCanonicalName()};
        }
        Cursor cursor = context.getContentResolver().query(URL,
                null,
                key,
                strings,
                String.valueOf(defaultValue));
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getString(0);
            cursor.close();
        }
        if(result == null || result.equals("null")){
            result = null;
        }
        return result;
    }
}
