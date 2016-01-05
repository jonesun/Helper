package jone.helper.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by jone.sun on 2015/9/2.
 */
@DatabaseTable
public class NotebookData implements Serializable{
//    @DatabaseField(generatedId = true, columnName = "_id") private int id;
//    @DatabaseField(generatedId = true) private int _id;

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField private String title;
    @DatabaseField(columnDefinition = "TEXT") private String content;
    @DatabaseField private String date;
    @DatabaseField private String updateDate;
    @DatabaseField private int color;

    public NotebookData(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
