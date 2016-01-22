package jone.helper.lib.model.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class GsonTool implements JsonTool {
    private Gson gson = new Gson();
    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T loadAs(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> T loadAs(byte[] bytes, Class<T> clazz) {
        return gson.fromJson(new String(bytes), clazz);
    }

    @Override
    public <T> T loadAs(Reader reader, Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }

    @Override
    public <T> List<T> loadAsList(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }

    @Override
    public <T> List<T> loadAsList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<T>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public <T> T deserialize(String json, Type type) throws JsonSyntaxException {
        return gson.fromJson(json, type);
    }

    @Override
    public boolean isGoodJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

}
