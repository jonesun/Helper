package jone.study.designPatterns.templateMethod;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Test {
    public static void main(String[] args) {
        AbstractComputer comp = new CoderComputer();
        comp.startUp();

        comp = new MilitaryComputer();
        comp.startUp();

    }
}
