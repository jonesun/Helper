package jone.helper.lib.ormlite;

import android.content.Context;

import jone.helper.lib.ormlite.entities.NotebookData;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookDao extends JoneOrmLiteBaseDao<NotebookData> {
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


}
