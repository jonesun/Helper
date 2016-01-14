package jone.helper.mvp.model.load;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple2;
import jone.helper.lib.volley.Method;
import jone.helper.mvp.model.netLoadData.NetLoadDataModel;
import jone.helper.mvp.model.netLoadData.VolleyJSONObjectLoadDataModel;

/**
 * Created by jone.sun on 2016/1/13.
 */
public abstract class JSONObjectLoadDataModel<T> implements LoadDataModel<T, JSONObject> {
    private static final String TAG = "JSONObjectLoadDataModel";
    private Callback<List<T>> callbacks;
    private NetLoadDataModel<JSONObject> netLoadDataModel;
    public JSONObjectLoadDataModel(){
        netLoadDataModel = new VolleyJSONObjectLoadDataModel();
    }

    @Override
    public void loadData(int pageIndex, final Callback<List<T>> callback) {
        this.callbacks = callback;
        Tuple2<String, Map<String, String>> config = getConfig(pageIndex);
        netLoadDataModel.loadData(Method.GET, config.v1, config.v2,
                new Callback<JSONObject>() {
                    @Override
                    public void onComplete(int resultCode, String message, JSONObject data) {
                        if(callbacks != null){
                            Tuple2<List<T>, Boolean> tuple2 = analysisData(data);
                            if(!tuple2.v2){ //没有下一页数据的标志
                                callbacks.onComplete(RESULT_CODE_NO_NEXT, message, tuple2.v1);
                            }else {
                                callbacks.onComplete(resultCode, message, tuple2.v1);
                            }
                        }
                    }
                });
    }

    @Override
    public void cancel() {
        netLoadDataModel.cancel();
    }
}
