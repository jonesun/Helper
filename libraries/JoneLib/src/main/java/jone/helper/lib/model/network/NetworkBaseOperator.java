package jone.helper.lib.model.network;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by jone.sun on 2016/1/22.
 */
public abstract class NetworkBaseOperator implements NetworkOperator {

    public abstract void StringGetRequest(NetworkRequest networkRequest, ResponseCallback<String> responseCallback);

    public abstract void StringPostRequest(NetworkRequest networkRequest, ResponseCallback<String> responseCallback);

    public abstract void JSONObjectGetRequest(NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback);

    public abstract void JSONObjectPostRequest(NetworkRequest networkRequest, ResponseCallback<JSONObject> responseCallback);

    @Override
    public <T> void request(NetworkRequest networkRequest, Class<T> clazz, final ResponseCallback<T> responseCallback) {
        check(networkRequest);
        switch (networkRequest.getMethod()){
            case NetworkRequest.Method.GET:
                if(clazz.getName().equals(String.class.getName())){
                    StringGetRequest(networkRequest, getResponseCallback(responseCallback, String.class));
                }else if(clazz.getName().equals(JSONObject.class.getName())){
                    JSONObjectGetRequest(networkRequest, getResponseCallback(responseCallback, JSONObject.class));
                }
                break;
            case NetworkRequest.Method.POST:
                if(clazz.getName().equals(String.class.getName())){
                    StringPostRequest(networkRequest, getResponseCallback(responseCallback, String.class));
                }else if(clazz.getName().equals(JSONObject.class.getName())){
                    JSONObjectPostRequest(networkRequest, getResponseCallback(responseCallback, JSONObject.class));
                }
                break;
        }
    }

    @Override
    public <T> void requestOfList(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<List<T>> responseCallback) {
        check(networkRequest);
    }

    private void check(NetworkRequest networkRequest){
        if(networkRequest == null){
            throw new RuntimeException("networkRequest cannot null");
        }
        String url = networkRequest.getUrl();
        if(url == null){
            throw new RuntimeException("url cannot null");
        }
    }

    private <T, H> ResponseCallback<H> getResponseCallback(final ResponseCallback<T> responseCallback, Class<H> clazz) {
        return new ResponseCallback<H>() {
            @Override
            public void onSuccess(H response, boolean fromCache) {
                if(responseCallback != null){
                    responseCallback.onSuccess((T) response, fromCache);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(responseCallback != null){
                    responseCallback.onFailure(e);
                }
            }
        };
    }
}
