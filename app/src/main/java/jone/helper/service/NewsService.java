package jone.helper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

import jone.helper.Constants;
import jone.helper.bean.News;
import jone.helper.lib.processDataOperator.ProcessDBDataOperator;
import jone.helper.lib.util.GsonUtils;
import jone.helper.util.DownloadHtmlFrom36krUtil;

public class NewsService extends Service {
    public NewsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveNews();
                } catch (Exception e) {
                    //e.printStackTrace();
                }finally {
                    stopSelf();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void saveNews() throws IOException {
        DownloadHtmlFrom36krUtil gcp = new DownloadHtmlFrom36krUtil();
        List<News> newses = gcp.get36krNews();
        if(newses != null && newses.size() > 0){
            ProcessDBDataOperator.getInstance(NewsService.this).delValueByKey(Constants.KEY_NEWS);
            ProcessDBDataOperator.getInstance(NewsService.this).putValue(Constants.KEY_NEWS, GsonUtils.toJson(newses));
            Intent intent = new Intent(Constants.BROADCAST_SAVE_NEWS_DONE);
            intent.putExtra(Constants.KEY_NEWS, (java.io.Serializable) newses);
            sendBroadcast(intent); //通知新闻已经保存成功
//            for(News news : newses){
//                System.err.println("title: " + news.getTitle()
//                        + ", url: " + news.getUrl()
//                        + ", imageUrl: " + news.getImageUrl()
//                        + ", from: " + news.getFrom());
//            }
        }
    }
}
