package jone.study.designPatterns.builder;

/**
 * Created by jone.sun on 2015/12/21.
 */
public abstract class Builder {
    public abstract void buildCPU(int core);

    public abstract void buildRAM(int gb);

    public abstract void buildOs(String os);

    public abstract Computer create();
}
