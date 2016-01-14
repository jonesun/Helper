package jone.helper.bean;

import java.io.Serializable;

/**
 * 天狗图片分类
 *
 *  * 分类ID	分类名称
 1	性感美女
 2	日韩美女
 3	丝袜美腿
 4	美女照片
 5	美女写真
 6	清纯美女
 7	性感车模
 * Created by jone.sun on 2016/1/13.
 */
public class TnGouGalleryClass implements Serializable{
    private int id; //分类id，需要查询该类下的列表就需要传入才参数
    private String name; //分类名称
    private String title; //分类的标题（网页显示的标题）
    private String keywords; //分类的关键词（网页显示的标题）
    private String description; //分类的描述（网页显示的标题）
    private int seq;//排序 从0。。。。10开始 分类的排序，从小到大的递增排序


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
