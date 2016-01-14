package jone.helper.mvp.model.picture;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import jone.helper.bean.LaiFuDaoPicture;
import jone.helper.lib.volley.Method;
import jone.helper.mvp.model.load.Callback;
import jone.helper.mvp.model.load.LoadDataModel;
import jone.helper.mvp.model.netLoadData.VolleyGsonLoadDataModel;

/**
 * http://api.laifudao.com/open/tupian.json
 * Created by jone.sun on 2016/1/12.
 */
public class LaiFuDaoPictureModel implements LoadDataModel<LaiFuDaoPicture, LaiFuDaoPicture> {
    private VolleyGsonLoadDataModel<List<LaiFuDaoPicture>> netLoadDataModel;
    public LaiFuDaoPictureModel(){
        netLoadDataModel = new VolleyGsonLoadDataModel<List<LaiFuDaoPicture>>();
    }

    @Override
    public Tuple2<String, Map<String, String>> getConfig(int pageIndex) {
        return Tuple.tuple("http://api.laifudao.com/open/tupian.json", null);
    }

    @Override
    public Tuple2<List<LaiFuDaoPicture>, Boolean> analysisData(LaiFuDaoPicture data) {
        return null;
    }

    @Override
    public void loadData(int pageIndex, final Callback<List<LaiFuDaoPicture>> callback) {
        Tuple2<String, Map<String, String>> config = getConfig(pageIndex);
        netLoadDataModel.loadData(Method.GET,
                config.v1, config.v2,
                new Callback<List<LaiFuDaoPicture>>() {
                    @Override
                    public void onComplete(int resultCode, String message, List<LaiFuDaoPicture> data) {
                        if(callback != null){
                            callback.onComplete(resultCode, message, data);
                        }
                    }
                });
    }

    @Override
    public void cancel() {
        netLoadDataModel.cancel();
    }
}
