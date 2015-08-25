package jone.helper.util;

import java.lang.reflect.Field;

import jone.helper.R;

/**
 * Created by jone.sun on 2015/8/25.
 */
public class ResUtil {
    
    public static int getDrawableResId(String name){
        return getResId(R.drawable.class, name);
    }

    public static int getMipmapResId(String name){
        return getResId(R.mipmap.class, name);
    }

    public static int getStringResId(String name){
        return getResId(R.string.class, name);
    }

    public static int getResId(Class ResClass, String name){
        try{
            Field field = ResClass.getField(name);
            return Integer.parseInt(field.get(null).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
