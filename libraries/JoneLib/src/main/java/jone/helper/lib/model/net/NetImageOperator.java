package jone.helper.lib.model.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import jone.helper.lib.volley.BitmapLruCache;
import jone.helper.lib.volley.VolleyCommon;
import jone.helper.lib.volley.VolleyErrorHelper;

/**
 * @author jone.sun on 2015/4/3.
 */
public class NetImageOperator {
    private static final String TAG = NetImageOperator.class.getSimpleName();
    private static NetImageOperator instance;
    public static NetImageOperator getInstance(Context context){
        if(instance == null){
            instance = new NetImageOperator(context);
        }
        return instance;
    }

    private VolleyCommon volleyCommon;
    private NetImageOperator(Context context){
        volleyCommon = VolleyCommon.getInstance(context);
    }

    private NetImageOperator(){

    }

    public ImageLoader getImageLoader(){
        return VolleyCommon.getImageLoader();
    }

    public void showToImageView(String url, ImageView imageView, int default_image, int failed_image){
        ImageLoader imageLoader = getImageLoader();
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                default_image, failed_image);
        imageLoader.get(url, listener);
    }

    /***
     * 如果指定的网络图片的宽度或高度大于这里的最大值，则会对图片进行压缩，
     * 指定成0的话就表示不管图片有多大，都不会进行压缩
     * @param url
     * @param imageView
     * @param default_image
     * @param failed_image
     * @param maxWidth
     * @param maxHeight
     */
    public void showToImageView(String url, ImageView imageView, int default_image, int failed_image, int maxWidth, int maxHeight){
        ImageLoader imageLoader = getImageLoader();
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                default_image, failed_image);
        imageLoader.get(url, listener, maxWidth, maxHeight);
    }

    public void requestByLoader(String url, final NetResponseCallback<Bitmap> responseCallback){
        ImageLoader imageLoader = getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(responseCallback != null){
                    responseCallback.onSuccess(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(responseCallback != null){
                    responseCallback.onFailure(VolleyErrorHelper.getMessage(error));
                }
            }
        });
    }

    /***
     * 如果指定的网络图片的宽度或高度大于这里的最大值，则会对图片进行压缩，
     * 指定成0的话就表示不管图片有多大，都不会进行压缩
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @param responseCallback
     */
    public void requestByLoader(String url, int maxWidth, int maxHeight, final NetResponseCallback<Bitmap> responseCallback){
        ImageLoader imageLoader = getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(responseCallback != null){
                    responseCallback.onSuccess(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(responseCallback != null){
                    responseCallback.onFailure(VolleyErrorHelper.getMessage(error));
                }
            }
        }, maxWidth, maxHeight);
    }

    public void request(String url,
                        final NetResponseCallback<Bitmap> responseCallback){
        volleyCommon.addToRequestQueue(new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if(responseCallback != null){
                            responseCallback.onSuccess(response);
                        }
                    }
                },
                0, 0, Bitmap.Config.RGB_565, getErrorListener(responseCallback)));
    }

    public void request(String url, int maxWidth, int maxHeight, Bitmap.Config decodeConfig,
                        final NetResponseCallback<Bitmap> responseCallback){
        volleyCommon.addToRequestQueue(new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if(responseCallback != null){
                            responseCallback.onSuccess(response);
                        }
                    }
                },
                maxWidth, maxHeight, decodeConfig, getErrorListener(responseCallback)));
    }

    private Response.ErrorListener getErrorListener(final NetResponseCallback responseCallback){
        return NetBaseOperator.getErrorListener(responseCallback);
    }

    public void cancelAll(String tag) {
        volleyCommon.cancelPendingRequests(tag);
    }
}
