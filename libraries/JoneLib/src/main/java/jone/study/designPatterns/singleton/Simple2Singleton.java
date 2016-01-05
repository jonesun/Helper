package jone.study.designPatterns.singleton;

/**
 * 方式二、double-check， 避免并发时创建了多个实例, 该方式不能完全避免并发带来的破坏.
 * Created by jone.sun on 2015/12/21.
 */
public class Simple2Singleton {
    private static Simple2Singleton instance = null;

    public static Simple2Singleton getInstace(){
        if(instance == null){
            synchronized (Simple2Singleton.class){
                if(instance == null){
                    instance = new Simple2Singleton();
                }
            }
        }
        return instance;
    }

    private Simple2Singleton(){}
}
