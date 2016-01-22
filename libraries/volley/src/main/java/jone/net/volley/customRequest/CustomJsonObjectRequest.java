package jone.net.volley.customRequest;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by jone.sun on 2016/1/22.
 */
public class CustomJsonObjectRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private boolean shouldCache;

    public CustomJsonObjectRequest(String url, Map<String, String> params,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener, boolean shouldCache) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
        this.shouldCache = shouldCache;
    }

    public CustomJsonObjectRequest(int method, String url, Map<String, String> params,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener, boolean shouldCache) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
        this.shouldCache = shouldCache;
    }

    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if(shouldCache){
                return Response.success(new JSONObject(jsonString), parseIgnoreCacheHeaders(response)); //强制缓存数据
            }else {
               return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }

    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    /***
     * 获取缓存的二进制数据
     * 一般的缓存数据的 key 都是Request 的 url，或者是实现request。getKey()自定义的。 知道了 key 就可以获取到对应的缓存：
     * 注意：不一定能获取到数据，原因是缓存任务线程还未被执行或者正在执行中！
     * @param mRequestQueue
     * @param key
     * @return
     */
    public static byte[] getDataInDiskCache(RequestQueue mRequestQueue, String key){
        Cache.Entry entry = mRequestQueue.getCache().get(key);
        return entry ==null? null : entry.data;

//        Cache cache = AppController.getInstance().getRequestQueue().getCache();
//        Entry entry = cache.get(url);
//        if(entry != null){
//            try {
//                String data = new String(entry.data, "UTF-8");
//                // handle data, like converting it to xml, json, bitmap etc.,
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//    }else{
//        // Cached response doesn't exists. Make network call here
//    }
    }

    /***
     * 强制刷新数据
     * 在允许缓存后，Volley 会根据缓存的有效期来决定是否重新联网获取数据。
     * 有时会有强制刷新数据的需求，这时就需要把缓存标记为无效
     * @param mRequestQueue
     * @param url
     */
    public static void forceRefresh(RequestQueue mRequestQueue, String url){
//        AppController.getInstance().getRequestQueue().getCache().invalidate(url, true);
        final Cache.Entry entry = mRequestQueue.getCache().get(url);
        if (entry != null && entry.data != null && entry.data.length > 0)
            if (!entry.isExpired()) {
                mRequestQueue.getCache().invalidate(url, true);
            }
    }

    public static Cache.Entry cache(NetworkResponse response, long maxAge){
        long now = System.currentTimeMillis();
        if(maxAge == 0) maxAge = 60;
        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long softExpire = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        softExpire = now + maxAge * 1000;
        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = entry.softTtl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }
}
