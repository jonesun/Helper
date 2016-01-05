package jone.study.dynamicProxy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class UserServiceImpl implements UserService {

    @Override
    public void add() {
        System.out.println("----- add -----");
    }
}
