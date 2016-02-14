package jone.helper.lib.util;

/**
 * Created by jone.sun on 2016/2/3.
 */
public abstract class SingletonUtils<T> {
    private T instance;

    protected abstract T newInstance();

    public final T getInstance() {
        if (instance == null) {
            synchronized (SingletonUtils.class) {
                if (instance == null) {
                    instance = newInstance();
                }
            }
        }
        return instance;
    }
}
