//package jone.helper.ui.loader;
//
//import android.content.Context;
//import android.support.v4.content.AsyncTaskLoader;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///**
// * Created by jone.sun on 2015/12/18.
// */
//public class NetworkLoader extends AsyncTaskLoader<String> {
//
//    private String result;
//
//    public NetworkLoader(Context context) {
//        super(context);
//    }
//
//    @Override
//    protected void onStartLoading() {
//        super.onStartLoading();
//        if (result != null) {
//            deliverResult(result);
//        } else {
//            forceLoad();
//        }
//    }
//
//    @Override
//    public String loadInBackground() {
//        String url = "http://gc.ditu.aliyun.com/geocoding?a=苏州市";
//        return doGet(url);
//    }
//
//    private String doGet(String url) {
//        BufferedReader in = null;
//        try {
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet(url);
//            HttpResponse response = client.execute(request);
//            in = new BufferedReader(new InputStreamReader(response.getEntity()
//                    .getContent()));
//
//            StringBuffer sb = new StringBuffer("");
//            String line = "";
//            String NL = System.getProperty("line.separator");
//            while ((line = in.readLine()) != null) {
//                sb.append(line + NL);
//            }
//            in.close();
//
//            String page = sb.toString();
//
//            return page;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return url;
//    }
//}
