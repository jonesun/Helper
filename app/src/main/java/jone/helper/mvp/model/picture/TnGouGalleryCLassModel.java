package jone.helper.mvp.model.picture;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import core.common.tuple.Tuple3;
import jone.helper.bean.TnGouGallery;
import jone.helper.bean.TnGouGalleryclass;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;

/**
 *
 * Created by jone.sun on 2016/1/12.
 */
class TnGouGalleryClassModel extends JSONObjectLoadDataModel<TnGouGalleryclass>  {
    private static final String TAG = "TnGouGalleryClassModel";

    public TnGouGalleryClassModel(){
        super();
    }

    @Override
    public Tuple3<String, Map<String, String>, Map<String, String>> getConfig(int pageIndex) {
        return Tuple.tuple("http://www.tngou.net/tnfs/api/classify", null, null);
    }

    @Override
    public Tuple2<List<TnGouGalleryclass>, Boolean> analysisData(JSONObject data) {
        List<TnGouGalleryclass> list = new ArrayList<>();
        try {
            if(data != null){
                if(data.has("tngou")){
                    list = GsonUtils.getGson().fromJson(data.getString("tngou"),
                            new TypeToken<List<TnGouGalleryclass>>() {}.getType());
                }
            }
        }catch (Exception e){
            Log.e(TAG, "analysisData", e);
        }

        return Tuple.tuple(list, false);
    }
}
