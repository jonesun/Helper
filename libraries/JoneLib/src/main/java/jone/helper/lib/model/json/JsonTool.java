package jone.helper.lib.model.json;

import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by jone.sun on 2016/1/14.
 */
public interface JsonTool {

    String toJson(Object obj);

    <T> T loadAs(String json, Class<T> clazz);

    <T> T loadAs(byte[] bytes, Class<T> clazz);

    <T> List<T> loadAsList(String json, Class<T> clazz);

    <T> T deserialize(String json, Type type);

    boolean isGoodJson(String json);

    <T> T loadAs(Reader reader, Class<T> clazz);

    <T> List<T> loadAsList(Reader reader, Type type);
}
