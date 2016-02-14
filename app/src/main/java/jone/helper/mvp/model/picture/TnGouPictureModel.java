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
import jone.helper.bean.TnGouPicture;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;

/**
 *
 * http://www.tngou.net/doc/gallery/29
 *
 * Created by jone.sun on 2016/1/12.
 */
public class TnGouPictureModel extends JSONObjectLoadDataModel<TnGouPicture>  {
    private static final String TAG = "TnGouPictureModel";

    private long showId;
    public TnGouPictureModel(long showId){
        super();
        this.showId = showId;
    }

    /***
     * 取得热点图片详情，通过热点id取得该对应详细内容信息
     * id	是	long	热点热词的id
     * @return
     */
    @Override
    public Tuple3<String, Map<String, String>, Map<String, String>> getConfig(int pageIndex) {
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + showId);
        return Tuple.tuple("http://www.tngou.net/tnfs/api/show?id=" + showId , null, null);
//        return Tuple.tuple("http://www.tngou.net/tnfs/api/show" , params);
    }

    @Override
    public Tuple2<List<TnGouPicture>, Boolean> analysisData(JSONObject data) {
        List<TnGouPicture> list = new ArrayList<>();
        try {
            if(data != null){
                if(data.has("list")){
                    list = GsonUtils.getGson().fromJson(data.getString("list"),
                            new TypeToken<List<TnGouPicture>>() {}.getType());
                }
            }
        }catch (Exception e){
            Log.e(TAG, "analysisData", e);
        }

        return Tuple.tuple(list, false);
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }
}
