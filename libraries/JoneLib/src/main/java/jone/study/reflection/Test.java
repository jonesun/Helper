package jone.study.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Test {
    public static void main(String[] args) throws Exception{
        Class<?> clazz = Class.forName("jone.study.reflection.Student");
        Constructor<?> constructor = clazz.getConstructor(String.class);
        Object obj = constructor.newInstance("jone.sun");
        System.out.println("obj>>" + obj.toString());
//        Constructor<?>[] constructors = clazz.getConstructors();

        //当你通过反射获取到Constructor、Method、Field后,
        // 在反射调用之前将此对象的 accessible 标志设置为true,以此来提升反射速度。
        // 值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。
        // 值为 false 则指示反射的对象应该实施 Java 语言访问检查。
//        constructor.setAccessible(true);
//        Method learnMethod = Student.class.getMethod("learn", String.class);
//        learnMethod.setAccessible(true);

        //getDeclaredMethod和getDeclaredMethods包含private、protected、default、public的函数,
        // 并且通过这两个函数获取到的只是在自身中定义的函数,从父类中集成的函数不能够获取到。
        // 而getMethod和getMethods只包含public函数,父类中的公有函数也能够获取到
        System.out.println();
        Student student = new Student("jone.sun");
        Method[] methods = student.getClass().getMethods();
        for(Method method : methods){
            System.out.println("method: " + method.getName());
        }
        System.out.println();
        Method[] declaredMethods = student.getClass().getDeclaredMethods();
        for(Method method : declaredMethods){
            System.out.println("declaredMethods: " + method.getName());
        }
    }
}
