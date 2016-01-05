package jone.study.designPatterns.templateMethod;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class MilitaryComputer extends AbstractComputer {


    @Override
    protected void checkHardware() {
        super.checkHardware();
        System.out.println("检查硬件防火墙");
    }

    @Override
    protected void login() {
        System.out.println("进行指纹之别等复杂的用户验证");
    }
}
