package jone.helper.mvp.model.picture;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import jone.helper.bean.LaiFuDaoPicture;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;
import jone.helper.mvp.model.load.StringLoadDataModel;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class LaiFuDaoPictureNewModel extends StringLoadDataModel<LaiFuDaoPicture> {
    @Override
    public Tuple2<String, Map<String, String>> getConfig(int pageIndex) {
        return Tuple.tuple("http://api.laifudao.com/open/tupian.json", null);
    }

    @Override
    public Tuple2<List<LaiFuDaoPicture>, Boolean> analysisData(String data) {
        List<LaiFuDaoPicture> list = new ArrayList<>();
        try {
            if(data != null){
                list = GsonUtils.getGson().fromJson(data,
                        new TypeToken<List<LaiFuDaoPicture>>() {}.getType());
            }
        }catch (Exception e){
            Log.e("LaiFuDaoPictureNewModel", "analysisData", e);
        }

        return Tuple.tuple(list, false);
    }
}
