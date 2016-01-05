package jone.study.designPatterns.proxy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Client {
    public static void main(String[] args) {
        AbstractObject obj = new ProxyObject();
        obj.operation();
    }
}
