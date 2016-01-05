package jone.study.designPatterns.abstractFactory;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class SmsSender implements Sender {

    @Override
    public void Send() {
        System.out.println("this is sms sender!");
    }
}
