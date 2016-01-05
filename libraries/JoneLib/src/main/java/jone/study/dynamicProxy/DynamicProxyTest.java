package jone.study.dynamicProxy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class DynamicProxyTest {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        MyInvocationHandler invocationHandler = new MyInvocationHandler(
                userService);

        UserService proxy = (UserService) invocationHandler.getProxy();
        proxy.add();
    }
}
