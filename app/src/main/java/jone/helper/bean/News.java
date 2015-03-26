package jone.helper.bean;

import java.io.Serializable;

/**
 * @author jone.sun on 2015/3/3.
 */
public class News implements Serializable{
    private String title;
    private String url;
    private String imageUrl;
    private String from;
    private long time;

    public News(){}

    public News(String title, String url, String imageUrl, String from, long time){
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.from = from;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
