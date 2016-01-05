package jone.helper.ui.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import jone.helper.bean.NotebookData;
import jone.helper.dao.NotebookDao;

/**
 * Created by jone.sun on 2015/12/18.
 */
public class NotebookLoader extends SimpleCursorLoader {
    public static final Uri uri = Uri.parse("content://jone.helper.notebook");
    ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Context context;
    private NotebookDao notebookDao;
    public NotebookLoader(Context context) {
        super(context);
        notebookDao = NotebookDao.getInstance(context);
    }

    @Override
    public Cursor loadInBackground() {
//        String[] columns = new String[] { "_id", "item", "description" };
//
//        MatrixCursor cursor= new MatrixCursor(columns);
//        cursor.addRow(new Object[] { 1, "Item A", "...." });


        Cursor cursor = null;
        CloseableIterator<NotebookData> iterator = null;
        try {
            // build your query
            QueryBuilder<NotebookData, Integer> qb = notebookDao.getDao().queryBuilder();
            // when you are done, prepare your query and build an iterator
            iterator = notebookDao.getDao().iterator(qb.prepare());
            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults)iterator.getRawResults();
            cursor = results.getRawCursor();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(iterator != null){
                iterator.closeQuietly();
            }
        }
        if (cursor != null) {
            //注册一下这个观察者
            cursor.registerContentObserver(mObserver);
            //这边也要注意 一定要监听这个uri的变化。但是如果你这个uri没有对应的provider的话
            //记得在你操作数据库的时候 通知一下这个uri
            cursor.setNotificationUri(context.getContentResolver(),
                    uri);

//            //操作完数据库要notify 不然loader那边收不到哦
//            context.getContentResolver().notifyChange(uri, null);
        }
        return cursor;
    }
}
