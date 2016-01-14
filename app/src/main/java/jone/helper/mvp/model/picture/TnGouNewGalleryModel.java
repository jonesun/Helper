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
import jone.helper.bean.TnGouGallery;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;

/**
 *
 * Created by jone.sun on 2016/1/12.
 */
public class TnGouNewGalleryModel extends JSONObjectLoadDataModel<TnGouGallery>  {
    private static final String TAG = "TnGouGalleryModel";
    private int id = 0;
    private int classify = 0;
    public TnGouNewGalleryModel(int id, int classify){
        super();
        this.id = id;
        this.classify = classify;
    }

    /***
     * 取得最新的图片，通过id取得大于该id的图片
     * rows	否	int	返回最新关键词的条数，默认rows=20
     classify	否	int	分类ID，取得该分类下的最新数据
     id	是	long	当前最新的图库关键词id
     * @return
     */
    @Override
    public Tuple2<String, Map<String, String>> getConfig(int pageIndex) {
        Map<String, String> params = new HashMap<>();
        params.put("classify", "" + classify);
        params.put("id", "" + id);
        Log.e(TAG, "getParams: " + params.toString());
        return Tuple.tuple("http://www.tngou.net/tnfs/api/news?id=" + id + "&classify=" + classify, null);
//        return Tuple.tuple("http://www.tngou.net/tnfs/api/news", params);
    }

    @Override
    public Tuple2<List<TnGouGallery>, Boolean> analysisData(JSONObject data) {
        List<TnGouGallery> list = new ArrayList<>();
        try {
            if(data != null){
                if(data.has("tngou")){
                    list = GsonUtils.getGson().fromJson(data.getString("tngou"),
                            new TypeToken<List<TnGouGallery>>() {}.getType());
                }
            }
        }catch (Exception e){
            Log.e(TAG, "analysisData", e);
        }

        return Tuple.tuple(list, false);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }
}
