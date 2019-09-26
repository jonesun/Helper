package jone.helper.mvp.model;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import core.common.tuple.Tuple3;
import jone.helper.Constants;
import jone.helper.bean.HistoryToday;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.util.GsonUtils;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;

/**
 * http://apistore.baidu.com/apiworks/servicedetail/1728.html
 * Created by jone.sun on 2016/2/14.
 */
public class HistoryTodayModel extends JSONObjectLoadDataModel<HistoryToday> {
    private int rows;
    public HistoryTodayModel(int rows){
        this.rows = rows;
    }
//    @Override
//    public int requestMethod() {
//        return NetworkRequest.Method.POST;
//    }

    @Override
    public Tuple3<String, Map<String, String>, Map<String, String>> getConfig(int pageIndex) {
        Map<String, String> header = new HashMap<>();
        header.put("apikey", Constants.BAIDU_API_STORE_API_KEY);
        Map<String, String> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        params.put("yue", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        params.put("ri", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        int random = (new Random().nextInt(100)) % 3;
        if(random == 0){
            random = 1;
        }
        params.put("type", String.valueOf(random));
        params.put("page", String.valueOf(pageIndex));
        params.put("rows", String.valueOf(rows));
        params.put("dtype", "JSON");
        params.put("format", "false");
        StringBuilder stringBuilder = new StringBuilder("http://apis.baidu.com/avatardata/historytoday/lookup?");
        stringBuilder.append("yue=").append(String.valueOf(calendar.get(Calendar.MONTH) + 1))
                .append("&ri=").append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))
                .append("&type=1")
                .append("&page=").append(String.valueOf(pageIndex))
        .append("&rows=").append(String.valueOf(rows)).append("&dtype=JSON&format=false");
        return Tuple.tuple(stringBuilder.toString()
                , header, params);
    }

    @Override
    public Tuple2<List<HistoryToday>, Boolean> analysisData(JSONObject data) {
        Log.e("sdsd", data + "");
        List<HistoryToday> historyTodayList = new ArrayList<>();
        if(data != null && data.has("error_code")){
            try {
                if(data.getInt("error_code") == 0);{
                    historyTodayList = GsonUtils.getGson().fromJson(data.getString("result"),
                            new TypeToken<List<HistoryToday>>() {}.getType());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                historyTodayList = new ArrayList<>();
            }
        }
        if(historyTodayList == null){
            historyTodayList = new ArrayList<>();
        }
        return Tuple.tuple(historyTodayList, historyTodayList.size() > 0);
    }
}
