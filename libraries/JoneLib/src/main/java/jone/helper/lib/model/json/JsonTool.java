package jone.helper.lib.model.json;

import java.util.List;

/**
 * Created by jone.sun on 2016/1/14.
 */
public interface JsonTool {

    String toJson(Object obj);

    <T> T loadAs(String json, Class<T> clazz);

    <T> T loadAs(byte[] bytes, Class<T> clazz);

    <T> List<T> loadAsList(String json, Class<T> clazz);

    boolean isGoodJson(String json);
}
