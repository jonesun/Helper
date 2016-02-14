package jone.helper.lib.model.network.okhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import core.common.tuple.Tuple;
import core.common.tuple.Tuple2;
import jone.helper.lib.model.json.GsonTool;
import jone.helper.lib.model.json.JsonTool;
import jone.helper.lib.model.net.okhttp.SimpleCookieJar;
import jone.helper.lib.model.network.NetworkOperator;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.lib.util.SystemUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jone.sun on 2016/1/15.
 */
public class OkHttpNetworkOperator implements NetworkOperator {
    public static final String TAG = "OkHttpNetworkOperator";
    private static OkHttpNetworkOperator mInstance;
    public static OkHttpNetworkOperator getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpNetworkOperator.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpNetworkOperator();
                }
            }
        }
        return mInstance;
    }
    private OkHttpNetworkOperator(){}

    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private JsonTool jsonTool;
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private Context content;
    private Map<String, List<Call>> callMap = new HashMap<>();

    @Override
    public NetworkOperator init(Context context){
        this.content = context;
        OkHttpClient.Builder build = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new SimpleCookieJar());//cookie enabled
        build.cache(new Cache(getCacheFile(context), cacheSize));
        mOkHttpClient = build.build();
        mDelivery = new Handler(Looper.getMainLooper());
        jsonTool = new GsonTool();
        return this;
    }

    @Override
    public <T>void request(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<T> responseCallback){
        if(networkRequest != null){
            Tuple2<String, Request> tuple2 = request(networkRequest);
            deliveryResult(tuple2.v1, responseCallback, tuple2.v2, clazz);
        }else {
            throw new RuntimeException("networkRequest cannot null");
        }
    }

    @Override
    public <T> void requestOfList(NetworkRequest networkRequest, Class<T> clazz, ResponseCallback<List<T>> responseCallback) {
        if(networkRequest != null){
            Tuple2<String, Request> tuple2 = request(networkRequest);
            deliveryResultOfList(tuple2.v1, responseCallback, tuple2.v2, clazz);
        }else {
            throw new RuntimeException("networkRequest cannot null");
        }
    }

    @Override
    public void cancel(String tag) {
        try {
            if(callMap.containsKey(tag)){
                List<Call> callList = callMap.get(tag);
                for(Call call : callList){
                    call.cancel();
                }
                callMap.remove(tag);
            }
        }catch (Exception e){}
    }

    @Override
    public void cancelAll() {
        try {
            Iterator<Map.Entry<String, List<Call>>> entries = callMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, List<Call>> entry = entries.next();
                List<Call> callList = entry.getValue();
                for(Call call : callList){
                    call.cancel();
                }
            }
            callMap.clear();
        }catch (Exception e){}

    }

    private Tuple2<String, Request> request(NetworkRequest networkRequest){
        Tuple2<String, Request.Builder> tuple2 = initRequestBuilder(networkRequest);
        Request request = tuple2.v2.build();
        Call call = mOkHttpClient.newCall(request);
        saveCallHistory(tuple2.v1, call);
        return Tuple.tuple(tuple2.v1, request);
    }

    private Tuple2<String, Request.Builder> initRequestBuilder(NetworkRequest networkRequest){
        Request.Builder builder = new Request.Builder();
        String url = networkRequest.getUrl();
        if(url == null){
            throw new RuntimeException("url cannot null");
        }
        int method = networkRequest.getMethod();
        Map<String, String> params = networkRequest.getParams();
        if(method == NetworkRequest.Method.GET){ //GET方法特殊设置参数
            builder = GETRequestBuilder(builder, url, params);
        }else if(method == NetworkRequest.Method.POST){
            builder = POSTRequestBuilder(builder, url, params);
        }
        Map<String, String> headerMap = networkRequest.getHeaders();
        if(headerMap != null){
            builder.headers(Headers.of(headerMap));
        }
        String tag = networkRequest.getTag();
        if(TextUtils.isEmpty(tag)){
            tag = url; //如果tag为空则设置为url
        }
        if(!networkRequest.isUsingCacheWithNoNetwork()  //没预设值或者有网络时，强制请求最新的资源
                || SystemUtil.hasNetWork(content)){
            builder.cacheControl(CacheControl.FORCE_NETWORK); //FORCE_NETWORK 即使在有缓存的情况下依然需要去后台请求最新的资源 FORCE_CACHE 强制只要使用缓存的数据
        }
        builder.tag(tag);
        return Tuple.tuple(tag, builder);
    }

    /***
     * GET方法的设置参数
     * @param builder
     * @param url
     * @param params
     * @return
     */
    private Request.Builder GETRequestBuilder(Request.Builder builder, String url, Map<String, String> params){
        if(params != null){
            HttpUrl httpUrl = HttpUrl.parse(url);
            HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder(url);
            Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                if (!TextUtils.isEmpty(entry.getValue())) { //过滤空的参数
                    httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            builder.url(httpUrlBuilder.build());
        }else {
            builder.url(url);
        }
        return builder;
    }

    /***
     * POST方法的设置参数
     * @param builder
     * @param url
     * @param params
     * @return
     */
    private Request.Builder POSTRequestBuilder(Request.Builder builder, String url, Map<String, String> params){
        FormBody.Builder formBodyBuilder  = new FormBody.Builder();
        Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (!TextUtils.isEmpty(entry.getValue())) { //过滤空的参数
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBodyBuilder.build();
        return builder.url(url).post(requestBody);
    }

    /***
     * 获取缓存文件夹
     * @param context
     * @return
     */
    private File getCacheFile(Context context){
        File cacheFile = null;
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                File[] externalCacheDirs = context.getExternalCacheDirs();
                if(externalCacheDirs != null && externalCacheDirs.length > 0){
                    cacheFile = externalCacheDirs[0];
                    for(File file : externalCacheDirs){
                        if(file != null && file.canRead() && file.canWrite() && file.getFreeSpace() > cacheFile.getFreeSpace()){
                            cacheFile = file;
                        }
                    }
                }
            }else {
                cacheFile = context.getExternalCacheDir();
            }
        }catch (Exception e){
            Log.e(TAG, "getCacheFile", e);
        }
        if(cacheFile == null || !cacheFile.exists()){
            cacheFile = context.getCacheDir();
        }
        return cacheFile;
    }

    private void saveCallHistory(String tag, Call call){
        List<Call> callList = new ArrayList<>();
        if(callMap.containsKey(tag)){
            callList = callMap.get(tag);
        }
        callList.add(call);
        callMap.put(tag, callList);
    }

    private void removeCallHistory(String tag, Call call){
        List<Call> callList = callMap.get(tag);
        for(Call c : callList){
            if(call == c){
                callList.remove(c);
            }
        }
        if(callList.size() == 0){
            callMap.remove(tag);
        }
    }

    private <T>void deliveryResult(final String tag, final ResponseCallback<T> callback, final Request request, final Class<T> clazz) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                //注意！這裏是backgroundThread
                if(callback != null){
                    sendFailCallback(callback, e);
                }
                removeCallHistory(tag, call);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                //注意！這裏是backgroundThread
                if(callback != null){
                    if (!response.isSuccessful()) {
                        sendFailCallback(callback, new IOException("Unexpected code " + response));
                    }else {
                        Log.e(TAG, "clazz.getName(): " + clazz.getName()
                                + " contentType: " + response.body().contentType().type()
                                + " networkResponse: " + response.networkResponse()
                                + " cacheResponse: " + response.cacheResponse());
                        if(clazz.getName().equals(String.class.getName())){
                            sendSuccessCallBack(callback, (T) response.body().string(), response.networkResponse() == null);
                        }else {
                            //final T data = jsonTool.loadAs(response.body().string(), clazz);
                             T data = jsonTool.loadAs(response.body().charStream(), clazz);
                            sendSuccessCallBack(callback, data, response.networkResponse() == null);
                        }
                    }
                    removeCallHistory(tag, call);
                }
            }
        });
    }

    private <T>void deliveryResultOfList(final String tag, final ResponseCallback<List<T>> callback, final Request request, final Class<T> clazz) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                //注意！這裏是backgroundThread
                if(callback != null){
                    sendFailCallback(callback, e);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                //注意！這裏是backgroundThread
                if(callback != null){
                    if (!response.isSuccessful()) {
                        sendFailCallback(callback, new IOException("Unexpected code " + response));
                    }else {
                        Log.e(TAG, "clazz.getName(): " + clazz.getName()
                                + " contentType: " + response.body().contentType().type()
                                + " networkResponse: " + response.networkResponse()
                                + " cacheResponse: " + response.cacheResponse());
                        try {
                            List<T> listData = jsonTool.loadAsList(response.body().charStream(), new  TypeToken<List<T>>(){}.getType());
                            sendSuccessCallBack(callback, listData, response.networkResponse() == null);
                        }catch (Exception e){
                            sendFailCallback(callback, e);
                        }

                    }
                    removeCallHistory(tag, call);
                }
            }
        });
    }

    private <T> void sendSuccessCallBack(final ResponseCallback<T> callback, final T t, final boolean fromCache) {

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(t, fromCache);
                }
            }
        });
    }

    private <T> void sendFailCallback(final ResponseCallback<T> callback, final Exception e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /***
     * 将OkHttpClient公开，便于外部拓展高级功能
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
