package jone.helper.mvp.model.netLoadData;
import java.util.Map;

import jone.helper.App;
import jone.helper.lib.model.net.NetResponseCallback;
import jone.helper.mvp.model.load.Callback;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class VolleyStringLoadDataModel extends VolleyBaseLoadDataModel<String> {
    @Override
    public void loadData(int method, String url, Map<String, String> params, final Callback<String> callback) {
        super.loadData(method, url, params, callback);
        App.getNetStringOperator().request(method, url, params,
                new NetResponseCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
//                        Log.e(TAG, "response: " + response);
                        if(callback != null){
                            callback.onComplete(Callback.RESULT_CODE_NORMAL, "success", response);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
//                        Log.e(TAG, "error: " + error);
                        if(callback != null){
                            callback.onComplete(Callback.RESULT_CODE_SERVER_ERROR, "error: " + error, null);
                        }
                    }
                });
    }

    @Override
    public void cancel() {
        App.getNetGsonOperator().cancelAll(TAG);
    }
}
