package jone.study.designPatterns.templateMethod;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class CoderComputer extends AbstractComputer {
    @Override
    protected void login() {
        System.out.println("码农只需要进行用户和密码验证就可以了");
    }
}
