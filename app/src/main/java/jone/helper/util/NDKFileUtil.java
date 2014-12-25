package jone.helper.util;

import java.io.File;

/**
 * Created by Administrator on 2014/12/22.
 */
public class NDKFileUtil {
    //暂时有问题
    @Deprecated
    public static boolean removeFile(String filePath){
        if(remove(filePath) == 0){
            return true;
        }
        return false;
    }

    public static boolean renameFile(String filePath, String outPath){
        String path = outPath.substring(0, outPath.lastIndexOf(File.separator) + 1);
        mkdirs(path);
        if(rename(filePath, outPath) == 0){
            return true;
        }
        return false;
    }

    //创建文件夹(包含上级目录)
    public static native int mkdirs(String filePath);

    //删除文件或者文件夹
    private static native int remove(String filePath);

    //重命名文件或者文件夹
    private static native int rename(String filePath, String outPath);

    static {
        System.loadLibrary("tp_file");
    }
}
