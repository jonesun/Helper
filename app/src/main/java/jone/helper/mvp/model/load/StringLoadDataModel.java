package jone.helper.mvp.model.load;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple2;
import jone.helper.lib.volley.Method;
import jone.helper.mvp.model.netLoadData.NetLoadDataModel;
import jone.helper.mvp.model.netLoadData.VolleyJSONObjectLoadDataModel;
import jone.helper.mvp.model.netLoadData.VolleyStringLoadDataModel;

/**
 * Created by jone.sun on 2016/1/14.
 */
public abstract class StringLoadDataModel<T> implements LoadDataModel<T, String> {
    private static final String TAG = "JSONObjectLoadDataModel";
    private Callback<List<T>> callbacks;
    private NetLoadDataModel<String> netLoadDataModel;
    public StringLoadDataModel(){
        netLoadDataModel = new VolleyStringLoadDataModel();
    }

    @Override
    public void loadData(int pageIndex, final Callback<List<T>> callback) {
        this.callbacks = callback;
        Tuple2<String, Map<String, String>> config = getConfig(pageIndex);
        netLoadDataModel.loadData(Method.GET, config.v1, config.v2,
                new Callback<String>() {
                    @Override
                    public void onComplete(int resultCode, String message, String data) {
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

