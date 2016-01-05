package jone.study.reflection;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class ReflectionUtil {

    /***
     * 加载指定的Class对象
     * @param className 加载的类的完整路径 例如"com.simple.Student". ( 常用方式 )
     * @return
     */
    public Class<?> classForName(String className){
        try {
            Class<?> clazz = Class.forName(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 加载指定的Class对象
     * @param className 加载的类的完整路径
     * @param shouldInitialize 是否要初始化该Class对
     * @param classLoader 指定加载该类的ClassLoader
     * @return
     */
    public Class<?> classForName(String className, boolean shouldInitialize, ClassLoader classLoader){
        try {
            Class<?> clazz = Class.forName(className, shouldInitialize, classLoader);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
