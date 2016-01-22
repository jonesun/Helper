//package jone.helper.lib.model.net.okhttp;
//
//import com.android.volley.toolbox.HurlStack;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import okhttp3.OkHttpClient;
//import okhttp3.OkUrlFactory;
//
///**
// * Created by jone.sun on 2015/12/9.
// */
//public class SimpleOkHttpStack extends HurlStack {
//    private final OkUrlFactory okUrlFactory;
//    public SimpleOkHttpStack() {
//        this(new OkUrlFactory(new OkHttpClient()));
//    }
//    public SimpleOkHttpStack(OkUrlFactory okUrlFactory) {
//        if (okUrlFactory == null) {
//            throw new NullPointerException("Client must not be null.");
//        }
//        this.okUrlFactory = okUrlFactory;
//    }
//    @Override
//    protected HttpURLConnection createConnection(URL url) throws IOException {
//        return okUrlFactory.open(url);
//    }
//
////    private final OkUrlFactory okUrlFactory;
////    public SimpleOkHttpStack() {
////        this(new OkHttpClient());
////    }
////    public SimpleOkHttpStack(OkHttpClient okHttpClient) {
////        if (okHttpClient == null) {
////            throw new NullPointerException("Client must not be null.");
////        }
////        this.okUrlFactory = new OkUrlFactory(okHttpClient);
////    }
////    @Override
////    protected HttpURLConnection createConnection(URL url) throws IOException {
////        return okUrlFactory.open(url);
////    }
//}
//
