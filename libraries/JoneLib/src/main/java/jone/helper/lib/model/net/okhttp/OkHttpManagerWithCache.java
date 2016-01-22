//package jone.helper.lib.model.net.okhttp;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.gson.internal.$Gson$Types;
//import com.squareup.okhttp.Cache;
//import com.squareup.okhttp.CacheControl;
//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.FormEncodingBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.net.CookieManager;
//import java.net.CookiePolicy;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import jone.helper.lib.model.json.GsonTool;
//import jone.helper.lib.model.json.JsonTool;
//
///**
// * 加缓存的OkHttpManager
// * Created by jone.sun on 2016/1/15.
// */
//public class OkHttpManagerWithCache {
//    private static OkHttpManagerWithCache mInstance;
//    private OkHttpClient mOkHttpClient;
//    private Handler mDelivery;
//    private JsonTool jsonTool;
//    private static final String TAG = "OkHttpClientManager";
//
//    private OkHttpManagerWithCache(Context context) {
//        mOkHttpClient = new OkHttpClient();
//        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
//        //cookie enabled
//        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
//        mDelivery = new Handler(Looper.getMainLooper());
//        jsonTool = new GsonTool();
//        File sdcache = context.getExternalCacheDir();
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        mOkHttpClient.setCache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
//    }
//
//    public static OkHttpManagerWithCache getInstance(Context context) {
//        if (mInstance == null) {
//            synchronized (OkHttpManagerWithCache.class) {
//                if (mInstance == null) {
//                    mInstance = new OkHttpManagerWithCache(context);
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    public Response requestSync(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url).build();
//        request = request.newBuilder()
//                .cacheControl(CacheControl.FORCE_NETWORK) //FORCE_NETWORK 即使在有缓存的情况下依然需要去后台请求最新的资源 FORCE_CACHE 强制只要使用缓存的数据
//                .build();
//        Call call = mOkHttpClient.newCall(request);
//
//        return call.execute();
//    }
//
//    public void request(String url, Callback callback) throws IOException {
//        Request request = new Request.Builder()
//                .url(url).build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(callback);
//    }
//
//    public void requestForUI(String url, final Callback callback) throws IOException {
//        Request request = new Request.Builder()
//                .url(url).build();
//        deliveryResult(callback, request);
//    }
//
//    public void requestForUI(String url, ResultCallback callback) throws IOException {
//        Request request = new Request.Builder()
//                .url(url).build();
//        deliveryResult(callback, request);
//    }
//
//    public void requestPost(String url, Map<String, String> params, Callback callback) throws IOException {
//        Request request = buildPostRequest(url, params);
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(callback);
//    }
//
//    private Request buildPostRequest(String url, Map<String, String> params) {
//        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
//        Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, String> entry = entries.next();
//            if (!TextUtils.isEmpty(entry.getValue())) { //过滤空的参数
//                encodingBuilder.add(entry.getKey(), entry.getValue());
//            }
//        }
//        RequestBody requestBody = encodingBuilder.build();
//        return new Request.Builder()
//                .url(url)
//                .post(requestBody).build();
//    }
//
//    private void deliveryResult(final Callback callback, final Request request) {
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(final Request request, final IOException e) {
//                //注意！這裏是backgroundThread
//                mDelivery.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onFailure(request, e);
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(final Response response) throws IOException {
//                //注意！這裏是backgroundThread
//                mDelivery.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            callback.onResponse(response);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            callback.onFailure(request, e);
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void deliveryResult(final ResultCallback callback, Request request) {
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, final IOException e) {
//                sendFailCallback(callback, e);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                try {
//                    String str = response.body().string();
//                    if (callback.mType == String.class) {
//                        sendSuccessCallBack(callback, str);
//                    } else {
//                        Object object = jsonTool.deserialize(str, callback.mType);
//                        sendSuccessCallBack(callback, object);
//                    }
//                } catch (final Exception e) {
//                    Log.e(TAG, "convert json failure", e);
//                    sendFailCallback(callback, e);
//                }
//
//            }
//        });
//    }
//
//    private void sendFailCallback(final ResultCallback callback, final Exception e) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                if (callback != null) {
//                    callback.onFailure(e);
//                }
//            }
//        });
//    }
//
//    private void sendSuccessCallBack(final ResultCallback callback, final Object obj) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                if (callback != null) {
//                    callback.onSuccess(obj);
//                }
//            }
//        });
//    }
//
//    /**
//     * http请求回调类,回调方法在UI线程中执行
//     *
//     * @param <T>
//     */
//    public static abstract class ResultCallback<T> {
//        Type mType;
//        public ResultCallback() {
//            mType = getSuperclassTypeParameter(getClass());
//        }
//        static Type getSuperclassTypeParameter(Class<?> subclass) {
//            Type superclass = subclass.getGenericSuperclass();
//            if (superclass instanceof Class) {
//                throw new RuntimeException("Missing type parameter.");
//            }
//            ParameterizedType parameterizedType = (ParameterizedType) superclass;
//            return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
//        }
//
//        /**
//         * 请求成功回调
//         *
//         * @param response
//         */
//        public abstract void onSuccess(T response);
//
//        /**
//         * 请求失败回调
//         *
//         * @param e
//         */
//        public abstract void onFailure(Exception e);
//    }
//}
