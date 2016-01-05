package jone.study.designPatterns.singleton;

/**
 * 单例模式-android studio2.0 Preview4 自带
 * Created by jone.sun on 2015/12/21.
 */
public class SimpleSingleton {
    private static SimpleSingleton ourInstance = new SimpleSingleton();

    public static SimpleSingleton getInstance() {
        return ourInstance;
    }

    private SimpleSingleton() {
    }
}
