package jone.helper.dao;

import android.content.Context;

import java.util.List;

import jone.helper.App;
import jone.helper.bean.Notebook;
import jone.helper.greendao.dao.NotebookDao;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookBeanDao {
    private static NotebookBeanDao instance;
    public static NotebookBeanDao getInstance(Context context){
        if(instance == null){
            instance = new NotebookBeanDao(context);
        }
        return instance;
    }
    private NotebookDao notebookDataDao;
    protected NotebookBeanDao(Context context) {
        notebookDataDao = App.getDaoSession(context.getApplicationContext()).getNotebookDao();
    }

    public long insertOrReplace(Notebook notebook) {
        notebook.setUpdateDate(System.currentTimeMillis());
        return notebookDataDao.insertOrReplace(notebook);
    }

    public List<Notebook> queryList(){
        return notebookDataDao.loadAll();
    }

    public void delete(Notebook notebook){
        notebookDataDao.delete(notebook);
    }

//    public void test(){
//        StringBuilder sb = new StringBuilder();
//        try {
//            GenericRawResults<String[]> rawResults =
//                    getDao().queryRaw("select * from NotebookData limit 1");
//            String[] firstResult = rawResults.getColumnNames();
//            for(int n = 0; n < firstResult.length; n++) {
//                sb.append(firstResult[n]).append(" ");
//            }
//            sb.append("\n");
//            Log.e("sssss", "测试: " + sb.toString());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public Cursor getCursor(){
//
//        // build your query
//        QueryBuilder<Foo, String> qb = fooDao.queryBuilder();
//        qb.where()...;
//// when you are done, prepare your query and build an iterator
//        CloseableIterator<Foo> iterator = dao.iterator(qb.prepare());
//        try {
//            // get the raw results which can be cast under Android
//            AndroidDatabaseResults results =
//                    (AndroidDatabaseResults)iterator.getRawResults();
//            Cursor cursor = results.getRawCursor();
//            ...
//        } finally {
//            iterator.closeQuietly();
//        }
//    }


}
