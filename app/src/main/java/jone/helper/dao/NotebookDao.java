package jone.helper.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;

import jone.helper.bean.NotebookData;
import jone.helper.model.db.HelperOrmLiteDao;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookDao extends HelperOrmLiteDao<NotebookData> {
    private static NotebookDao instance;
    public static NotebookDao getInstance(Context context){
        if(instance == null){
            instance = new NotebookDao(context);
        }
        return instance;
    }
    protected NotebookDao(Context context) {
        super(context);
    }


    public void test(){
        StringBuilder sb = new StringBuilder();
        try {
            GenericRawResults<String[]> rawResults =
                    getDao().queryRaw("select * from NotebookData limit 1");
            String[] firstResult = rawResults.getColumnNames();
            for(int n = 0; n < firstResult.length; n++) {
                sb.append(firstResult[n]).append(" ");
            }
            sb.append("\n");
            Log.e("sssss", "测试: " + sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
