package jone.helper.mvp.model.picture;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import core.common.tuple.Tuple3;
import jone.helper.bean.TnGouGallery;
import jone.helper.bean.TnGouPicture;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;

/**
 *
 * Created by jone.sun on 2016/1/12.
 */
public class TnGouGalleryModel extends JSONObjectLoadDataModel<TnGouGallery>  {
    private static final String TAG = "TnGouGalleryModel";

    private int clazzId = 0;
    public TnGouGalleryModel(int classId){
        super();
        this.clazzId = classId;
    }

    /***
     * 取得图片分类，可以通过分类id取得热词列表
     * page	否	int	请求页数，默认page=1
     rows	否	int	每页返回的条数，默认rows=20
     id	否	int	分类ID，默认返回的是全部。这里的ID就是指分类的ID
     * @return
     */
    @Override
    public Tuple3<String, Map<String, String>, Map<String, String>> getConfig(int pageIndex) {
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + clazzId);
        params.put("page", "" + pageIndex);
        return Tuple.tuple("http://www.tngou.net/tnfs/api/list?page=" + pageIndex + "&id=" + clazzId, null, null);
//        return Tuple.tuple("http://www.tngou.net/tnfs/api/list", params);
    }

    @Override
    public Tuple2<List<TnGouGallery>, Boolean> analysisData(JSONObject data) {
        List<TnGouGallery> list = new ArrayList<>();
        try {
            if(data != null){
                if(data.has("tngou")){
                    list = GsonUtils.getGson().fromJson(data.getString("tngou"),
                            new TypeToken<ArrayList<TnGouGallery>>() {}.getType());
                }
            }
        }catch (Exception e){
            Log.e(TAG, "analysisData", e);
        }

        return Tuple.tuple(list, true);
    }

    public void setClazzId(int clazzId) {
        this.clazzId = clazzId;
    }
}
