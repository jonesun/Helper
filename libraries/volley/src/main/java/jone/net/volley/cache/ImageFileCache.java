package jone.net.volley.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 文件缓存
 * Created by jone.sun on 2015/12/3.
 */
public class ImageFileCache {
    private static final String CACHE_DIR = "ImgCache";
    private static final String WHOLESALE_CONV = ".cache";

    private static final int MB = 1024 * 1024;
    private static final int CACHE_SIZE = 10;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

    public ImageFileCache(){
        //清理文件缓存
        removeCache(getDirectory());
    }

    /***
     * 从缓存中获取图片
     * @param url
     * @return
     */
    public Bitmap getImage(final String url){
        final String path = getDirectory() + File.separator + convertUrlToFileName(url);
        File file = new File(path);
        if(file.exists()){
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if(bmp == null){
                file.delete();
            }else {
                updataFileTime(path);
                return bmp;
            }
        }
        return null;
    }

    /**
     * 将图片存入文件缓存
     * @param bm
     * @param url
     */
    public void saveBitmap(Bitmap bm, String url){
        if(bm == null){
            return;
        }
        if(FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()){
            //SD卡空间不足
            return;
        }
        String filename = convertUrlToFileName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dir + File.separator + filename);
        try{
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (FileNotFoundException e){
            Log.w("ImageFileCache", "FileNotFoundException");
        }catch (IOException e){
            Log.w("ImageFileCache", "IOException");
        }
    }

    /***
     * 计算存储目录下的文件大小
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     * @param dirPath
     * @return
     */
    private boolean removeCache(String dirPath){
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if(files == null){
            return true;
        }
        if(Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)){
            return false;
        }

        int dirSize = 0;
        for(int i = 0; i < files.length; i++){
            if(files[i].getName().contains(WHOLESALE_CONV)){
                dirSize += files[i].length();
            }
        }

        if(dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()){
            int removeFactor = (int)((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifSort());
            for(int i = 0; i < removeFactor; i++){
                if(files[i].getName().contains(WHOLESALE_CONV)){
                    files[i].delete();
                }
            }
        }

        if(freeSpaceOnSd() <= CACHE_SIZE){
            return false;
        }

        return true;
    }

    /***
     * 修改文件的最后修改时间
     * @param path
     */
    public void updataFileTime(String path){
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /**
     * 计算sdcard上的剩余空间
     * @return
     */
    private int freeSpaceOnSd(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double)statFs.getAvailableBlocks() * (double)statFs.getBlockSizeLong()) /MB;
        return (int)sdFreeMB;
    }

    /***
     * 将url转成文件名
     * @param url
     * @return
     */
    private String convertUrlToFileName(String url){
        String[] strings = url.split("/");
        return strings[strings.length - 1] + WHOLESALE_CONV;
    }

    /**
     * 获取缓存目录
     * @return
     */
    private String getDirectory(){
        String dir = getSDPath() + File.separator + CACHE_DIR;
        return dir;
    }

    /**
     * 获取SD卡路径
     * @return
     */
    private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(sdCardExist){
            sdDir = Environment.getExternalStorageDirectory(); //获取根目录
        }
        if(sdDir != null){
            return sdDir.toString();
        }else {
            return "";
        }
    }

    /***
     * 根据文件的最后修改时间进行排序
     */
    private class FileLastModifSort implements Comparator<File>{

        @Override
        public int compare(File lhs, File rhs) {
            if(lhs.lastModified() > rhs.lastModified()){
                return 1;
            }else if(lhs.lastModified() == rhs.lastModified()){
                return 0;
            }else {
                return -1;
            }
        }
    }
}
