package jone.study.designPatterns.strategy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Test {
    //执行方法
    public static double calc(Strategy strategy, double paramA, double paramB) {
        Calc calc = new Calc();
        calc.setStrategy(strategy);
        return calc.calc(paramA, paramB);
    }

    public static void main(String[] args) {
        double paramA = 5;
        double paramB = 21;

        System.out.println("------------ 策略模式  ----------------");
        System.out.println("加法结果是：" + calc(new Strategy.AddStrategy(), paramA, paramB));
        System.out.println("减法结果是：" + calc(new Strategy.SubStrategy(), paramA, paramB));
        System.out.println("乘法结果是：" + calc(new Strategy.MultiStrategy(), paramA, paramB));
        System.out.println("除法结果是：" + calc(new Strategy.DivStrategy(), paramA, paramB));
    }
}
