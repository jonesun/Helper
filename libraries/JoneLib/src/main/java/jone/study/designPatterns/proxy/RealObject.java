package jone.study.designPatterns.proxy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class RealObject extends AbstractObject {
    @Override
    public void operation() {
        //一些操作
        System.out.println("一些操作");
    }
}
