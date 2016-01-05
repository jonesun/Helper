package jone.helper.ui.loader;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedQuery;

import jone.helper.bean.NotebookData;
import jone.helper.lib.model.ormlite.extras.AndroidBaseDaoImpl;
import jone.helper.lib.model.ormlite.extras.OrmliteCursorLoader;

/**
 * Created by jone.sun on 2015/12/18.
 */
public class ORMLiteNotebookLoader extends OrmliteCursorLoader<NotebookData> {
    public ORMLiteNotebookLoader(Context context,
                                 AndroidBaseDaoImpl<NotebookData, ?> dao,
                                 PreparedQuery<NotebookData> query) {
        super(context, dao, query);
    }


}
