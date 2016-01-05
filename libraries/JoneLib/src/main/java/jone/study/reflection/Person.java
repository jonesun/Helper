package jone.study.reflection;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Person {
    String mName;
    public Person(String aName) {
        mName = aName;
    }
    private void sayHello(String friendName) {
        System.out.println(mName + " say hello to " + friendName);
    }
    protected void showMyName() {
        System.out.println("My name is " + mName);
    }
    public void breathe() {
        System.out.println(" take breathe ");
    }
}
