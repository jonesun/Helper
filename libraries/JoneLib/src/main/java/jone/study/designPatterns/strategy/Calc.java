package jone.study.designPatterns.strategy;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Calc {
    private Strategy strategy;
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public double calc(double paramA, double paramB) {
        // TODO Auto-generated method stub
        // doing something
        if (this.strategy == null) {
            throw new IllegalStateException("你还没有设置计算的策略");
        }
        return this.strategy.calc(paramA, paramB);
    }
}
