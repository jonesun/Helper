package jone.helper.lib.model.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by jone_admin on 13-12-6.
 */
public abstract class JoneBaseOrmLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = JoneBaseOrmLiteHelper.class.getSimpleName();

    public JoneBaseOrmLiteHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public void upgradeClazzField(SQLiteDatabase sqLiteDatabase, Class clazz){
        try {
            Log.e(TAG, "onUpgrade>>clazz: " + clazz);
            DatabaseTable databaseTable = (DatabaseTable) clazz.getAnnotation(DatabaseTable.class);
            String tabName = databaseTable.tableName();
            if(tabName == null || tabName.length() == 0){
                tabName = clazz.getSimpleName();
            }
            String tmpTabName = "_temp_" + tabName;

            StringBuilder stringBuilder = new StringBuilder();
            sqLiteDatabase.execSQL("ALTER TABLE " + tabName + " RENAME TO " + tmpTabName);
            TableUtils.createTableIfNotExists(connectionSource, clazz);
            GenericRawResults<String[]> rawResultsTmp =
                    getDao(clazz).queryRaw("select * from " + tmpTabName + " limit 1");
            GenericRawResults<String[]> rawResults =
                    getDao(clazz).queryRaw("select * from " + tabName + " limit 1");
            String[] columnNamesTmp = rawResultsTmp.getColumnNames();
            String[] columnNames = rawResults.getColumnNames();
            int incrementalColumnNum = columnNames.length - columnNamesTmp.length;
            if(incrementalColumnNum > 0){
                for(int i = 0; i < incrementalColumnNum; i++){
                    stringBuilder.append(",");
                    stringBuilder.append("''");
                }
            }
            String sql = "insert into " + tabName + " select *" + stringBuilder.toString() + " from " + tmpTabName;
            sqLiteDatabase.execSQL(sql);
            sqLiteDatabase.execSQL("DROP TABLE " + tmpTabName);
            Log.e(TAG, "sql: " + sql + "\r\n"
                    + "columnNamesTmp: " + columnNamesTmp.length + "\r\n"
                    + "columnNames: " + columnNames.length);

        }catch (Exception e){
            Log.e(TAG, "error>>upgradeClassField：" + clazz.getName(), e);
            try {
                TableUtils.dropTable(connectionSource, clazz, true);
            } catch (SQLException e1) {
                Log.e(TAG, "error>>upgradeClassField dropTable：" + clazz.getName(), e1);
            }
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