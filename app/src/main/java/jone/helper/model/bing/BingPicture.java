package jone.helper.model.bing;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jone.sun on 2015/9/21.
 */
public class BingPicture implements Serializable {
    private String url;
    private String copyright;
    private List<BingPictureHs> hs;
    private List<BingPictureMsg> msg;

    public BingPicture(){}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public List<BingPictureHs> getHs() {
        return hs;
    }

    public void setHs(List<BingPictureHs> hs) {
        this.hs = hs;
    }

    public List<BingPictureMsg> getMsg() {
        return msg;
    }

    public void setMsg(List<BingPictureMsg> msg) {
        this.msg = msg;
    }
}
