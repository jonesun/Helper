package jone.helper.lib.util;

import java.util.Map;

/**
 * Created by jone.sun on 2016/1/5.
 */
public class MapUtil {
    /**
     * <pre>
     * 用于从Map对象中取得值.
     * 使用方法:
     *  比如有一个Map对象中有如下结构:
     *    {
     *        "id":123,
     *        "name":"mike"
     *        "images":[
     *            {"imageId":"x1", "url":"/x1.png"},
     *            {"imageId":"x2", "url":"/x2.png"}
     *        ],
     *        "layer1":{
     *            "layer2":"layer2value"
     *        }
     *    }
     *
     *  // 返回原始类型
     *  int id = MapUtil.opt(map,"id",-1);
     *
     *  // 返回对象类型
     *  Integer id = MapUtil.opt(map,"id",null);
     *  String name = MapUtil.opt(map,"user","");
     *
     *  // 使用javascript方式获得更深层次的值
     *  String layer2value = MapUtil.opt(map,"layer1.layer2","");
     *
     * </pre>
     * @param data Map类型的数据
     * @param path "." 分割的路径描述.如: user.userName
     * @param defaultValue 路径不存在时返回的默认值
     * @return
     */
    public static <T> T get(Map data, String path, T defaultValue) {
        Object result = data;
        String[] paths = path.split("\\.");
        for (String path2 : paths) {
            if (result instanceof Map && result != null) {
                result = ((Map) result).get(path2);
            } else {
                return defaultValue;
            }
        }
        return result == null ? defaultValue : (T) result;
    }
}
