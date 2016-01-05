package jone.study.designPatterns.strategy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public interface Strategy {
    public double calc(double paramA, double paramB);

    //加法的具体实现策略
    public class AddStrategy implements Strategy {
        @Override
        public double calc(double paramA, double paramB) {
            // TODO Auto-generated method stub
            System.out.println("执行加法策略...");
            return paramA + paramB;
        }
    }

    //减法的具体实现策略
    public class SubStrategy implements Strategy {
        @Override
        public double calc(double paramA, double paramB) {
            // TODO Auto-generated method stub
            System.out.println("执行减法策略...");
            return paramA - paramB;
        }
    }

    //乘法的具体实现策略
    public class MultiStrategy implements Strategy {
        @Override
        public double calc(double paramA, double paramB) {
            // TODO Auto-generated method stub
            System.out.println("执行乘法策略...");
            return paramA * paramB;
        }
    }

    //除法的具体实现策略
    public class DivStrategy implements Strategy {
        @Override
        public double calc(double paramA, double paramB) {
            // TODO Auto-generated method stub
            System.out.println("执行除法策略...");
            if (paramB == 0) {
                throw new IllegalArgumentException("除数不能为0!");
            }
            return paramA / paramB;
        }
    }
}
