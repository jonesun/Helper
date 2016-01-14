package jone.helper.lib.model.imageLoader;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.InputStream;

import jone.helper.lib.volley.VolleyCommon;

/**
 * 采用Glide库的imageLoader
 * compile 'com.github.bumptech.glide:glide:3.6.1'
 * compile 'com.github.bumptech.glide:volley-integration:1.3.1' //可选
 * compile 'com.github.bumptech.glide:okhttp-integration:1.3.1' //可选
 * Created by jone.sun on 2016/1/12.
 */
public class GlideImageLoader implements ImageLoader {
    private static GlideImageLoader ourInstance = new GlideImageLoader();

    public static GlideImageLoader getInstance() {
        return ourInstance;
    }

    private GlideImageLoader() {
    }

    public GlideImageLoader init(Application application) {
        //Glide使用Volley
        Glide.get(application)
                .register(GlideUrl.class, InputStream.class,
                        new VolleyUrlLoader.Factory(VolleyCommon.getInstance(application).getmRequestQueue()));

        //okHttp
//        Glide.get(application)
//                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
        return this;
    }

    /***
     * 加载本地图片
     * 下表是.load()可以传入的参数及说明
     * 参数	说明
     * .load(String string)	string可以为一个文件路径、uri或者url
     * .load(Uri uri)	uri类型
     * .load(File file)	文件
     * .load(Integer resourceId)	资源Id,R.drawable.xxx或者R.mipmap.xxx
     * .load(byte[] model)	byte[]类型
     * .load(T model)	自定义类型
     * @param context
     * @param imageView
     * @param url
     * @param image_loading
     * @param image_error
     */
    @Override
    public void display(Context context, ImageView imageView, String url,
                        int image_loading, int image_error) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        //他還有很多功能，直接夾在 load() 跟 into() 中間即可
//        Glide.with(viewHolder.imageView.getContext())
//
//                .load(url)
//
//                .error(R.drawable.ic_person)//load失敗的Drawable
//
//                .placeholder()//loading時候的Drawable
//
//                .animate()//設置load完的動畫
//
//                .centerCrop()//中心切圖, 會填滿
//
//                .fitCenter()//中心fit, 以原本圖片的長寬為主
//
//                .into(imageView);

//        Target target = Glide.with(context)
//        ...
//        .into(new SimpleTarget<Bitmap>(width, height) {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                callback.onDone(resource);
//            }
//
//            @Override
//            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                callback.onDone(null);
//            }
//        });
//
//// At some point later, if you want to cancel the load:
//        Glide.clear(target);
        Glide.with(context).load(url).placeholder(image_loading)
                .error(image_error)
                .crossFade() //图片的淡入淡出动画效果
                .into(imageView);
    }

//    @Override
//    public void display(Context context, String url, ImageView imageView,
//                        int image_loading, int image_error,
//                        final ImageLoaderListener imageLoaderListener) {
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .listener(new RequestListener<String, Bitmap>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        if (imageLoaderListener != null) {
//                            imageLoaderListener.onDone(null);
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        if (imageLoaderListener != null) {
//                            imageLoaderListener.onDone(resource);
//                        }
//                        return false;
//                    }
//                })
//                .placeholder(image_loading)
//                .error(image_error)
//                .into(imageView);
//    }

    @Override
    public void getBitmap(Context context, String url, final ImageLoaderListener imageLoaderListener) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (imageLoaderListener != null) {
                            imageLoaderListener.onDone(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (imageLoaderListener != null) {
                            imageLoaderListener.onDone(null);
                        }
                    }
                });
    }


}
