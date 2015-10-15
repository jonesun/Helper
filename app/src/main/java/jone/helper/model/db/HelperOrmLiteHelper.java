package jone.helper.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import jone.helper.bean.NotebookData;
import jone.helper.lib.model.ormlite.JoneBaseOrmLiteHelper;


/**
 * Created by jone_admin on 13-12-6.
 */
public class HelperOrmLiteHelper extends JoneBaseOrmLiteHelper {
    private static final String TAG = HelperOrmLiteHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "db_jone";
    private static final int DATABASE_VERSION = 3;
    private static HelperOrmLiteHelper instance = null;
    
    public static HelperOrmLiteHelper getInstance(Context context){
    	if(instance == null){
    		instance = new HelperOrmLiteHelper(context);
    	}
    	return instance;
    }
    private HelperOrmLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, NotebookData.class);
        } catch (SQLException e) {
            Log.e(TAG, "数据表创建失败 ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
    }

}