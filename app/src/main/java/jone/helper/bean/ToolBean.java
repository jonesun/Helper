package jone.helper.bean;

import android.view.View;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/9/18.
 */
public class ToolBean implements Serializable {
    private int id;
    private String name;
    private Integer imageSrcResource;
    private Integer backgroundResource;
    private View.OnClickListener onClickListener;

    public ToolBean(){}

    public ToolBean(int id, String name, Integer imageSrcResource, Integer backgroundResource, View.OnClickListener onClickListener){
        this.id = id;
        this.name = name;
        this.imageSrcResource = imageSrcResource;
        this.backgroundResource = backgroundResource;
        this.onClickListener = onClickListener;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImageSrcResource() {
        return imageSrcResource;
    }

    public void setImageSrcResource(Integer imageSrcResource) {
        this.imageSrcResource = imageSrcResource;
    }

    public Integer getBackgroundResource() {
        return backgroundResource;
    }

    public void setBackgroundResource(Integer backgroundResource) {
        this.backgroundResource = backgroundResource;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
