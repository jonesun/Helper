package jone.helper.lib.model.network;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jone.sun on 2016/1/15.
 */
public class NetworkRequest {
    private Builder builder;
    private int method; //请求方式 GET、POST、PUT、DELETE
    private String url;
    private Map<String, String> headers; //请求头
    private Map<String, String> params;   //请求参数
    private String tag; //设置tag,用于取消请求
    private int cacheControl; //缓存策略
    private boolean usingCacheWithNoNetwork; //当无网络时使用缓存
    private boolean writeDebugLog; //打印debugLog
    private NetworkRequest(){}
    private NetworkRequest(Builder builder){
        this.builder = builder;
    }

    public int getMethod() {
        return builder.method;
    }

    public String getUrl() {
        return builder.url;
    }

    public Map<String, String> getHeaders() {
        return builder.headers;
    }

    public Map<String, String> getParams() {
        return builder.params;
    }

    public String getTag() {
        return initTag(builder.tag);
    }

    public int getCacheControl() {
        return cacheControl;
    }

    public boolean isUsingCacheWithNoNetwork() {
        return builder.usingCacheWithNoNetwork;
    }

    public boolean isWriteDebugLog() {
        return builder.writeDebugLog;
    }

    public static class Builder{
        private int method; //请求方式 GET、POST、PUT、DELETE
        private String url;
        private Map<String, String> headers; //请求头
        private Map<String, String> params;   //请求参数
        private String tag; //设置tag,用于取消请求
        private int cacheControl; //缓存策略
        private boolean usingCacheWithNoNetwork; //当无网络时使用缓存
        private boolean writeDebugLog; //打印debugLog
        public Builder(){

        }

        public Builder get(){
            method = Method.GET;
            return this;
        }

        public Builder post(){
            method = Method.POST;
            return this;
        }

        public Builder method(@Method int method){
            this.method = method;
            return this;
        }

        public Builder addParam(String name, @NonNull String value) {
            if(params == null){
                params = new HashMap<>();
            }
            params.put(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            if(headers == null){
                headers = new HashMap<>();
            }
            headers.put(name, value);
            return this;
        }

        public Builder params(Map<String, String> params){
            this.params = params;
            return this;
        }

        public Builder headers(Map<String, String> headers){
            this.headers = headers;
            return this;
        }

        public Builder tag(String tag){
            this.tag = tag;
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public void setCacheControl(@CacheControl int cacheControl) {
            this.cacheControl = cacheControl;
        }

        public Builder setUsingCacheWithNoNetwork(boolean usingCacheWithNoNetwork) {
            this.usingCacheWithNoNetwork = usingCacheWithNoNetwork;
            return this;
        }

        public Builder setWriteDebugLog(boolean writeDebugLog) {
            this.writeDebugLog = writeDebugLog;
            return this;
        }

        public NetworkRequest build(){
            return new NetworkRequest(this);
        }

    }

    private String initTag(String tag){
        if(TextUtils.isEmpty(tag)){
            tag = getUrl(); //如果tag为空则设置为url
        }
        return tag;
    }

    public @interface Method { //HTTP请求方式
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public @interface CacheControl { //缓存策略
        int FIRST_USE_NETWORK = 0; //优先使用网络数据，获取不到则采用缓存数据
        int FORCE_NETWORK = 1; //强制使用网络数据
        int FIRST_USE_CACHE = 2; //优先使用缓存数据
        int FORCE_CACHE = 3;  //强制使用缓存数据
        int FIRST_USE_CACHE_THEN_USE_NETWORK = 4; //先使用缓存数据，然后获取网络数据
    }
}
