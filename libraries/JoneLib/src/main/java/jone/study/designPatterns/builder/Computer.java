package jone.study.designPatterns.builder;

/**
 * Created by jone.sun on 2015/12/21.
 */
public abstract class Computer {
    protected int mCpuCore = 1;
    protected int mRamSize = 0;
    protected String mOs = "Dos";

    protected Computer(){}

    public abstract void setCPU(int core);

    public abstract void setRAM(int gb);

    public abstract void setOs(String os);

    @Override
    public String toString() {
//        return super.toString();
        return "Computer [mCpuCore=" + mCpuCore + ", mRamSize=" + mRamSize
                + ", mOs=" + mOs + "]";
    }
}
