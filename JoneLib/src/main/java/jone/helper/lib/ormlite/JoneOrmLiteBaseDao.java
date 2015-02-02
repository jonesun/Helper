package jone.helper.lib.ormlite;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JoneOrmLiteBaseDao<T> {
	 private static final String TAG = JoneOrmLiteBaseDao.class.getSimpleName();
	    private Class<T> entityClass;
	    private Dao<T, Integer> dao;
	    @SuppressWarnings("unchecked")
		protected JoneOrmLiteBaseDao(Context context){
	        entityClass = (Class<T>) ((ParameterizedType) getClass()
	                .getGenericSuperclass()).getActualTypeArguments()[0];
	        try {
	            dao = JoneOrmLiteHelper.getInstance(context).getDao(entityClass);
	        } catch (SQLException e) {
	            Log.e(TAG, entityClass + "  dao获取失败", e);
	        }
	    }


	    protected int create(T t){

	        int result = -1;
	        try {
	            result = getDao().create(t);
	        } catch (SQLException e) {
	            Log.e(TAG, t + " 添加失败", e);
	        }
	        return result;
	    }

	    protected int delete(T t){
	        int result = -1;
	        try {
	            result = getDao().delete(t);
	        } catch (SQLException e) {
	            Log.e(TAG, t + " 删除失败", e);
	        }
	        return result;
	    }
	    protected  int deleteAll(){
	        int result = -1;
	        try {
	            result = getDao().delete(queryList());
	        } catch (SQLException e) {
	            Log.e(TAG,  " 删除失败", e);
	        }
	        return result;
	    }

	    protected T queryByColumn(String columnName, Object value){
	        T result = null;
	        try {
	            List<T> list = getDao().queryBuilder().where().eq(columnName, value).query();
	            if(list != null && list.size() > 0){
	                result = list.get(0);
	            }
	        } catch (SQLException e) {
	            Log.e(TAG, columnName + ": " + value + " 查询失败", e);
	        }
	        return result;
	    }

	    protected List<T> queryByValue(String columnName, Object value){
	        List<T> result = null;
	        try {
	            List<T> list = getDao().queryBuilder().where().eq(columnName, value).query();
	            if(list != null && list.size() > 0){
	                result = list;
	            }
	        } catch (SQLException e) {
	            Log.e(TAG, columnName + ": " + value + " 查询失败", e);
	        }
	        return result;
	    }

	    protected List<T> queryList(){
	         List<T> results = null;
	        try {
	            results = getDao().queryForAll();
	        } catch (SQLException e) {
	            Log.e(TAG, "查询失败", e);
	        }
	        return results;
	    }

	    protected long getCount(){
	        long l = 0;
	        try {
	            l = getDao().queryBuilder().countOf();
	        } catch (SQLException e) {
	            Log.e(TAG, "查询失败, getCount: " + l, e);
	        }
	        return l;
	    }

	    public List<T> query(String orderByColumnName, boolean isAscending, int limit){
	        List<T> list = new ArrayList<T>();
	        try {
	            list = getDao().queryBuilder().orderBy(orderByColumnName, isAscending).limit(intToLong(limit)).query();
	        } catch (SQLException e) {
                Log.e(TAG, "查询失败", e);
	            e.printStackTrace();
	        }
	        return list;
	    }
	    protected Dao.CreateOrUpdateStatus createOrUpdate(T t){
	        Dao.CreateOrUpdateStatus result = null;
	        try {
	            result = dao.createOrUpdate(t);
	        } catch (SQLException e) {
                Log.e(TAG, "createOrUpdate", e);
	        }
	        return result;
	    }

	    protected int update(T t){
	        int result = -1;
	        try {
	            result = getDao().update(t);
	        } catch (SQLException e) {
	            Log.e(TAG, t + "更新失败", e);
	        }
	        return result;
	    }

	    public Dao<T, Integer> getDao() {
	        return dao;
	    }
	    
	    public static long intToLong(int i){
	        long l = 0;
	        try{
	            l = Long.parseLong(String.valueOf(i));
	        }catch (Exception e){
	            Log.e("intToLong", "转换失败", e);
	        }
	        return l;
	    }
}
