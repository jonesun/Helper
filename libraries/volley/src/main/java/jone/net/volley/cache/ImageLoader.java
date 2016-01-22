package jone.net.volley.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载工具类
 * Created by jone.sun on 2015/12/4.
 */
public class ImageLoader {
    private static ImageLoader instance;

    private ExecutorService executorService; //线程池
    private ImageMemoryCache memoryCache; //内存缓存
    private ImageFileCache fileCache; //文件缓存
    private Map<String, ImageView> taskMap; //存放任务
    private boolean allowLoad = true; //是否允许加载图片

    private ImageLoader(Context context){
        //获取当前系统的CPU数目
        int cpuNums = Runtime.getRuntime().availableProcessors();
        //根据系统资源情况灵活定义线程池大小
        this.executorService = Executors.newFixedThreadPool(cpuNums + 1);
        this.memoryCache = new ImageMemoryCache(context);
        this.fileCache = new ImageFileCache();
        this.taskMap = new HashMap<String, ImageView>();
    }

    /**
     * 使用单例，保证整个应用中只有一个线程池和一份内存缓存和文件缓存
     * @param context
     * @return
     */
    public static ImageLoader getInstance(Context context){
        if(instance == null){
            instance = new ImageLoader(context);
        }
        return instance;
    }

    /**
     * 恢复为初始可加载图片的状态
     */
    public void restore(){
        this.allowLoad = true;
    }

    /***
     * 锁住时不允许加载图片
     */
    public void lock(){
        this.allowLoad = false;
    }

    /***
     * 解锁时加载图片
     */
    public void unlock(){
        this.allowLoad = true;
        doTask();
    }

    public void addTask(String url, ImageView imageView){
        Bitmap bitmap = memoryCache.getBitmapFromCache(url);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else {
            synchronized (taskMap){
                /**
                 * 因为ListView或GridView的原理是用上面移出屏幕的item去填充下面新显示的item
                 * 这里的imageView是item里的内容，所以这里的taskMap保存的始终是当前屏幕内的所有item
                 */
                imageView.setTag(url);
                taskMap.put(Integer.toString(imageView.hashCode()), imageView);
            }
            if(allowLoad){
                doTask();
            }
        }
    }

    /***
     * 加载存放任务中的所有图片
     */
    private void doTask(){
        synchronized (taskMap){
            Collection<ImageView> collection = taskMap.values();
            for(ImageView imageView : collection){
                if(imageView != null){
                    if(imageView.getTag() != null){
                        loadImage((String)imageView.getTag(), imageView);
                    }
                }
            }
            taskMap.clear();
        }
    }

    private void loadImage(String url, ImageView imageView){
        this.executorService.submit(new TaskWithResult(new TaskHandler(url, imageView), url));
    }

    /***
     * 获取图片
     * 首先从内存缓存中获取
     * 然后是文件缓存
     * 最后是从网络中获取
     * @param url
     * @return
     */
    private Bitmap getBitmap(String url){
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if(result == null){
            result = fileCache.getImage(url);
            if(result == null){
                result = ImageGetFromHttp.downloadBitmap(url);
                if(result != null){
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            }else {
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }
    /***子线程任务***/
    private class TaskWithResult implements Callable<String>{
        private String url;
        private Handler handler;

        public TaskWithResult(Handler handler, String url){
            this.handler = handler;
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            Message message = new Message();
            message.obj = getBitmap(url);
            if(message.obj != null){
                handler.sendMessage(message);
            }
            return null;
        }
    }

    /***完成消息***/
    private class TaskHandler extends Handler{
        String url;
        ImageView imageView;

        public TaskHandler(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            /***查看ImageView需要显示的图片是否被改变**/
            if(imageView.getTag().equals(url)){
                if(msg.obj != null){
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
