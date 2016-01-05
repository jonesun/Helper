package jone.study.designPatterns.builder;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class AppleComputer extends Computer {
    @Override
    public void setCPU(int core) {
        mCpuCore = core;
    }

    @Override
    public void setRAM(int gb) {
        mRamSize = gb;
    }

    @Override
    public void setOs(String os) {
        mOs = os;
    }
}
