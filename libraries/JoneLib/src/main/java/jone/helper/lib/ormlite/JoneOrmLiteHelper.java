package jone.helper.lib.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import jone.helper.lib.ormlite.entities.LongTextString;


/**
 * Created by jone_admin on 13-12-6.
 */
public class JoneOrmLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = JoneOrmLiteHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "db_jone";
    private static final int DATABASE_VERSION = 2;
    private static JoneOrmLiteHelper instance = null;
    
    public static JoneOrmLiteHelper getInstance(Context context){
    	if(instance == null){
    		instance = new JoneOrmLiteHelper(context);
    	}
    	return instance;
    }
    private JoneOrmLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, LongTextString.class);
        } catch (SQLException e) {
            Log.e(TAG, "数据表创建失败 ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, LongTextString.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "更新数据库失败", e);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        // lookup the dao, possibly invoking the cached database config
        Dao<T, ?> dao = DaoManager.lookupDao(connectionSource, clazz);
        if (dao == null) {
            // try to use our new reflection magic
            DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
            if (tableConfig == null) {
                /**
                 * TODO: we have to do this to get to see if they are using the deprecated annotations like
                 * {@link DatabaseFieldSimple}.
                 */
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, clazz);
            } else {
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, tableConfig);
            }
        }

        @SuppressWarnings("unchecked")
        D castDao = (D) dao;
        return castDao;
    }
}