package jone.helper.model.bing;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPictureMsg implements Serializable {
    private String title;
    private String text;
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
