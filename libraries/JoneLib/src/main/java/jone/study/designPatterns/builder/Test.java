package jone.study.designPatterns.builder;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class Test {
    public static void main(String[] args) {
        // 构建器
        Builder builder = new ApplePCBuilder();
        // Director
        Director pcDirector = new Director(builder);
        // 封装构建过程, 4核, 内存2GB, Mac系统
        pcDirector.construct(4, 2, "Mac OS X 10.9.1");
        // 构建电脑, 输出相关信息
        System.out.println("Computer Info : " + builder.create().toString());
    }
}
