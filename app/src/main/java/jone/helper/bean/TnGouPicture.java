package jone.helper.bean;

import java.io.Serializable;

/**
 注意事项

 img字段返回的是不完整的图片路径src，
 需要在前面添加【http://tnfs.tngou.net/image】或者【http://tnfs.tngou.net/img】
 前者可以再图片后面添加宽度和高度，如：http://tnfs.tngou.net/image/top/default.jpg_180x120

 网页分享，这里我们有专门的网页版【跨平台支持手机浏览器】
 需要在id添加【http://www.tngou.net/tnfs/show/】
 结构 http://www.tngou.net/tnfs/show/+id

 如：http://www.tngou.net/tnfs/show/1 http://www.tngou.net/tnfs/show/10

 http://tnfs.tngou.net/image/ext/160112/cb9ebf0ee7b7413be44c171fc6e54280.jpg
 * Created by jone.sun on 2016/1/12.
 */
public class TnGouPicture implements Serializable{
    private int id;
    private int gallery; //图片库
    private String src; //图片地址

    public int getId() {
        return id;
    }

    public int getGallery() {
        return gallery;
    }

    public String getSrc() {
        return "http://tnfs.tngou.net/image" + src;
    }
}