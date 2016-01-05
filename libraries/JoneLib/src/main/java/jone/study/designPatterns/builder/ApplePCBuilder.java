package jone.study.designPatterns.builder;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class ApplePCBuilder extends Builder {
    private Computer mApplePc = new AppleComputer();
    @Override
    public void buildCPU(int core) {
        mApplePc.setCPU(core);
    }

    @Override
    public void buildRAM(int gb) {
        mApplePc.setRAM(gb);
    }

    @Override
    public void buildOs(String os) {
        mApplePc.setOs(os);
    }

    @Override
    public Computer create() {
        return mApplePc;
    }
}
