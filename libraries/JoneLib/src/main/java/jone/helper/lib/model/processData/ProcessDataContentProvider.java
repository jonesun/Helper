package jone.helper.lib.model.processData;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;

import java.util.Map;

public class ProcessDataContentProvider extends ContentProvider {
    private static final String TAG = ProcessDataContentProvider.class.getSimpleName();
    private static final String SP_NAME = "sp_process";
    private static final String AUTHORITY = "jone.helper.lib.ProcessDataContentProvider";
    private final static int SP = 1;
    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "sp/#", SP);
    }
    public static final Uri SP_URI = Uri.parse("content://" + AUTHORITY + "/sp/1");

    private SharedPreferences sharedPreferences;

    public ProcessDataContentProvider() {
    }

    @Override
    public boolean onCreate() {
        sharedPreferences = getContext().getSharedPreferences(SP_NAME, getContext().MODE_PRIVATE);
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int flag = URI_MATCHER.match(uri);
        //Log.e(TAG, "insert>>" + uri.toString() + " ," + flag);
        if(values == null){
            return null;
        }
        switch (flag){
            case SP:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    for (Map.Entry<String, Object> item : values.valueSet()) {
                        String key = item.getKey(); // getting key
                        Object value = item.getValue(); // getting value
                        saveToSharedPreferences(editor, key, value);
                    }
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    for (String key : values.keySet()) {
                        saveToSharedPreferences(editor, key, values.get(key));
                    }
                }
                editor.apply();
                break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int flag = URI_MATCHER.match(uri);
        // Log.e(TAG, "delete>>" + uri.toString() + " ," + flag);
        int result = 0;
        switch (flag){
            case SP:
                if(selection != null){
                    result = sharedPreferences.edit().remove(selection).commit() ? 1: 0;
                }
                break;
        }
        return result;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int flag = URI_MATCHER.match(uri);
        switch (flag) {
            case SP:
                return "vnd.android.cursor.item/jone.helper.lib.sp";
        }
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String defaultStr) {
        int flag = URI_MATCHER.match(uri);
        //Log.e(TAG, "query>>" + uri.toString() + " ," + flag);
        String[] names = new String[]{"result"};
        String[] values = new String[]{"null"};
        MatrixCursor cursor = new MatrixCursor(names);
        switch (flag){
            case SP:
                if(selection != null ){
                    if(selectionArgs != null && selectionArgs.length > 0){
                        String type = selectionArgs[0];
                        if(type.equals(String.class.getCanonicalName())){
                            values = new String[]{sharedPreferences.getString(selection, defaultStr)};
                        }else if(type.equals(Integer.class.getCanonicalName())){
                            values = new String[]{String.valueOf(sharedPreferences.getInt(selection, Integer.parseInt(defaultStr)))};
                        }else if(type.equals(Boolean.class.getCanonicalName())){
                            values = new String[]{String.valueOf(sharedPreferences.getBoolean(selection, Boolean.parseBoolean(defaultStr)))};
                        }else if(type.equals(Float.class.getCanonicalName())){
                            values = new String[]{String.valueOf(sharedPreferences.getFloat(selection, Float.parseFloat(defaultStr)))};
                        }else if(type.equals(Long.class.getCanonicalName())){
                            values = new String[]{String.valueOf(sharedPreferences.getLong(selection, Long.parseLong(defaultStr)))};
                        }
                    }else if(selectionArgs == null){//默认值为null时要单独处理
                        values = new String[]{sharedPreferences.getString(selection, defaultStr)};
                    }

                }
                cursor.addRow(values);
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int flag = URI_MATCHER.match(uri);
        //Log.e(TAG, "update>>" + uri.toString() + " ," + flag);
        int result = -1;
        switch (flag){
            case SP:
                break;
        }
        return result;
    }

    private void saveToSharedPreferences(SharedPreferences.Editor editor, String key, Object value){
        if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if (value instanceof String){
            editor.putString(key, (String) value);
        }else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }
    }

}
