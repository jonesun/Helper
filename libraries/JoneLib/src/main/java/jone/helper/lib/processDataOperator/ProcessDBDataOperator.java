package jone.helper.lib.processDataOperator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import jone.helper.lib.contentProvider.ProcessDataContentProvider;

/**
 * 跨进程读写到SQLite
 * 需在XML的注册ProcessDataContentProvider
 * Created by jone.sun on 2015/2/2.
 */
public class ProcessDBDataOperator implements ProcessDataOperator {
    private static ProcessDBDataOperator instance;
    private Context context;
    private static final Uri URL = ProcessDataContentProvider.DB_URI;

    public static ProcessDBDataOperator getInstance(Context context){
        if(instance == null){
            instance = new ProcessDBDataOperator(context);
        }
        return instance;
    }

    private ProcessDBDataOperator(Context context){
        this.context = context;
    }

    @Override
    public <T> void putValue(String key, T value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(key, String.valueOf(value));
        context.getContentResolver().insert(URL, contentValues);
    }

    @Override
    public void batchPutValue(ContentValues contentValues) {
        context.getContentResolver().insert(URL, contentValues);
    }

    @Override
    public int getValueByKey(String key, int defaultValue) {
        try{
            return Integer.parseInt(getResult(context, key));
        }catch (Exception e){
            return defaultValue;
        }
    }

    @Override
    public float getValueByKey(String key, float defaultValue) {
        try{
            return Float.parseFloat(getResult(context, key));
        }catch (Exception e){
            return defaultValue;
        }
    }

    @Override
    public long getValueByKey(String key, long defaultValue) {
        try{
            return Long.parseLong(getResult(context, key));
        }catch (Exception e){
            return defaultValue;
        }
    }

    @Override
    public boolean getValueByKey(String key, boolean defaultValue) {
        try{
            return Boolean.parseBoolean(getResult(context, key));
        }catch (Exception e){
            return defaultValue;
        }
    }

    @Override
    public String getValueByKey(String key, String defaultValue) {
        return getResult(context, key);
    }

    @Override
    public boolean delValueByKey(String key) {
        context.getContentResolver().delete(URL, key, null);
        return true;
    }

    private static String getResult(Context context, String key){
        String result = null;
        Cursor cursor = context.getContentResolver().query(URL,
                null,
                key,
                null,
                null);
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
