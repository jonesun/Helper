package jone.study.designPatterns.abstractFactory;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class SendMailFactory implements Provider {

    @Override
    public Sender produce(){
        return new MailSender();
    }
}
