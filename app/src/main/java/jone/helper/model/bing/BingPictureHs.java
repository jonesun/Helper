package jone.helper.model.bing;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPictureHs implements Serializable{
    private String desc;
    private String link;
    private String query;
    private int locx;
    private int locy;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLocx() {
        return locx;
    }

    public void setLocx(int locx) {
        this.locx = locx;
    }

    public int getLocy() {
        return locy;
    }

    public void setLocy(int locy) {
        this.locy = locy;
    }
}
