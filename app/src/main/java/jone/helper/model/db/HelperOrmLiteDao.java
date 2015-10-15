package jone.helper.model.db;

import android.content.Context;

import jone.helper.lib.model.ormlite.JoneBaseOrmLiteDao;
import jone.helper.lib.model.ormlite.JoneBaseOrmLiteHelper;


public class HelperOrmLiteDao<T> extends JoneBaseOrmLiteDao<T> {

	protected HelperOrmLiteDao(Context context) {
		super(context);
	}

	@Override
	public JoneBaseOrmLiteHelper getOrmLiteHelper() {
		return HelperOrmLiteHelper.getInstance(getContext());
	}

}
