package jone.study.designPatterns.facade;

/**
 * Created by jone.sun on 2015/12/21.
 */
/**
 * 电源控制系统
 */
class PowerSystem {
    public void powerOn() {
        System.out.println("开机");
    }

    public void powerOff() {
        System.out.println("关机");
    }
}
