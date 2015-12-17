package jone.helper.ui.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jone.sun on 2015/12/16.
 */
public class CityCursorLoader extends CursorLoader {
    private Context mContext;
    SQLiteDatabase database;
    public CityCursorLoader(Context context) {
        super(context);
        mContext = context;
    }
    /**
     * 查询数据等操作放在这里执行
     */
    @Override
    protected Cursor onLoadInBackground() {
        Cursor cursor = null;
        try {
            PackageInfo info = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            String packageNames = info.packageName;
            String DB_PATH = "/data/data/" + packageNames + "/databases/";
            String DB_NAME = "weather_city.db";
            if (!new File(DB_PATH + DB_NAME).exists()) {
                File f = new File(DB_PATH);
                if (!f.exists()) {
                    f.mkdir();
                }
                InputStream is = mContext.getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            }

            database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
            cursor = database.query("weathercity", new String[]{"id as _id", "name"}, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    @Override
    protected void onReset() {
        super.onReset();
        if(database != null){
            database.close();
        }
    }
}
