//package jone.net.volley.customRequest;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//
//import java.util.Map;
//
///**
// * Created by jone.sun on 2016/1/22.
// */
//public abstract class UsingCookiesRequest<T> extends Request<T> {
//    private static final String HEADER_COOKIE = "Cookie";
//    private Cookie[] mCookies;
//
//    public UsingCookiesRequest(String url, Map<String, String> requestBody, Cookie[] cookies, Class<T> clazz,
//                               Response.Listener<T> listener, Response.ErrorListener errorListner) {
//        super(url, requestBody, clazz, listener, errorListner);
//        mCookies = cookies;
//    }
//
//    public UsingCookiesRequest(String url, Cookie[] cookies, Class<T> clazz, Response.Listener<T> listener,
//                               Response.ErrorListener errorListner) {
//        this(url, null,cookies, clazz, listener, errorListner);
//    }
//
//    @Override
//    public Map<String, String> getHeaders() {
//        Map<String, String> headers = super.getHeaders();
//
//        StringBuilder builder = new StringBuilder();
//        for(int i=0;i<mCookies.length;i++){
//            Cookie cookie = mCookies[i];
//            builder.append(cookie.getName())
//                    .append("=")
//                    .append(cookie.getValue());
//            if(i < mCookies.length -1)
//                builder.append("; ");
//        }
//        headers.put(HEADER_COOKIE, builder.toString());
//        return headers;
//    }
//
//}