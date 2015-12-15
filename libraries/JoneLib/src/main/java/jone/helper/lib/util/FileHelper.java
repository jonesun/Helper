package jone.helper.lib.util;

import android.content.Context;
import android.os.Environment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jone.sun on 2015/11/6.
 */
public class FileHelper {

    /**
     * Check if the primary "external" storage device is available.
     * 判断sd卡可用
     *
     * @return
     */
    public static boolean hasSDCardMounted() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void save(Context context, String fileName, String fileContent) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        outputStream.write(fileContent.getBytes());
        outputStream.close();
    }

    public String read(Context context, String fileName) throws IOException {
        FileInputStream inputStream = context.openFileInput(fileName);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = inputStream.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        inputStream.close();
        return sb.toString();
    }
}
