package jone.study.designPatterns.abstractFactory;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Test {
    public static void main(String[] args) {
        Provider provider = new SendMailFactory();
        Sender sender = provider.produce();
        sender.Send();
    }
}
