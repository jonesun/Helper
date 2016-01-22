package jone.helper.lib.model.net.okhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jone.helper.lib.model.json.GsonTool;
import jone.helper.lib.model.json.JsonTool;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * // Create new gson object
 final Gson gson = new Gson();
 // Get a handler that can be used to post to the main thread
 client.newCall(request).enqueue(new Callback() {
 // Parse response using gson deserializer
 @Override
 public void onResponse(final Response response) throws IOException {
 // Process the data on the worker thread
 GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
 // Access deserialized user object here
 }
 }
 http://www.iana.org/assignments/media-types/media-types.xhtml MediaType
 https://github.com/square/okhttp/wiki/Recipes
 * Created by jone.sun on 2016/1/15.
 */
public class OkHttpManager {
    private static final String TAG = "OkHttpManager";
    private static OkHttpManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private JsonTool jsonTool;
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB

    private OkHttpManager() {

    }

    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }

    public OkHttpManager init(Context context){
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

    public Response requestSync(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url).build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    public void request(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
//        call.cancel();
    }

    public void request(String url, Map<String, String> params, Callback callback) throws IOException {
        HttpUrl httpUrl = HttpUrl.parse(url);
        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder(url);
        Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (!TextUtils.isEmpty(entry.getValue())) { //过滤空的参数
                httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpUrlBuilder.build()).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
//        call.cancel();
    }

    public void request(String url, Callback callback, String tag) throws IOException {
        Request request = new Request.Builder()
                .url(url).tag(tag).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
//        call.cancel();
    }

    public void requestForUI(String url, final Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url).build();
        deliveryResult(callback, request);
    }

    public void requestForUI(String url, ResultCallback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url).build();
        deliveryResult(callback, request);
    }

    public void requestPost(String url, Map<String, String> params, Callback callback) throws IOException {
        Request request = buildPostRequest(url, params);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void cancel(String tag) {
//        mOkHttpClient.cancel(tag);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailCallback(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    sendSuccessCallBack(callback, file);
                } catch (IOException e) {
                    sendFailCallback(callback, e);
                } finally {
                    try {
                        if (is != null) is.close();
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex > 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    private Request buildPostRequest(String url, Map<String, String> params) {
        FormBody.Builder builder  = new FormBody.Builder();
        Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (!TextUtils.isEmpty(entry.getValue())) { //过滤空的参数
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody).build();
    }

    private void deliveryResult(final Callback callback, final Request request) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                //注意！這裏是backgroundThread
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(call, e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                //注意！這裏是backgroundThread
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onResponse(call, response);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onFailure(call, e);
                        }
                    }
                });
            }
        });
    }

    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailCallback(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String str = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessCallBack(callback, str);
                    } else {
                        Object object = jsonTool.deserialize(str, callback.mType);
                        sendSuccessCallBack(callback, object);
                    }
                } catch (final Exception e) {
                    Log.e(TAG, "convert json failure", e);
                    sendFailCallback(callback, e);
                }

            }
        });
    }

    private void sendFailCallback(final ResultCallback callback, final Exception e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private void sendSuccessCallBack(final ResultCallback callback, final Object obj) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(obj);
                }
            }
        });
    }

    /**
     * http请求回调类,回调方法在UI线程中执行
     *
     * @param <T>
     */
    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
        }

        /**
         * 请求成功回调
         *
         * @param response
         */
        public abstract void onSuccess(T response);

        /**
         * 请求失败回调
         *
         * @param e
         */
        public abstract void onFailure(Exception e);
    }

    private File getCacheFile(Context context){
        File cacheFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File[] externalCacheDirs = context.getExternalCacheDirs();
            if(externalCacheDirs != null && externalCacheDirs.length > 0){
                cacheFile = externalCacheDirs[0];
                for(File file : externalCacheDirs){
                    if(file.getFreeSpace() > cacheFile.getFreeSpace()){
                        cacheFile = file;
                    }
                }
            }
        }else {
            cacheFile = context.getExternalCacheDir();
        }
        if(cacheFile == null || !cacheFile.exists()){
            cacheFile = context.getCacheDir();
        }
        return cacheFile;
    }
}
