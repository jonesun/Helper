package jone.study.designPatterns.singleton;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Simple3Singleton {
    /* 私有构造方法，防止被实例化 */
    private Simple3Singleton() {
    }

    /* 此处使用一个内部类来维护单例 */
    private static class SingletonFactory {
        private static Simple3Singleton instance = new Simple3Singleton();
    }

    /* 获取实例 */
    public static Simple3Singleton getInstance() {
        return SingletonFactory.instance;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return getInstance();
    }
}
