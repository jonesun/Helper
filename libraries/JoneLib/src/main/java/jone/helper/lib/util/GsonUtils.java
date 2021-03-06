package jone.helper.lib.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jone.sun on 2015/3/3.
 */
public class GsonUtils {
    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T loadAs(String json, Class<T> clazz){
        return gson.fromJson(json, clazz);
    }

    public <T> T loadAs(byte[] bytes, Class<T> clazz) {
        return gson.fromJson(new String(bytes), clazz);
    }

    public static <T> List<T> loadAsList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static <T> T deserialize(String json, Type type) throws JsonSyntaxException {
        return gson.fromJson(json, type);
    }

    public static boolean isGoodJson(String json) {
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
