package jone.helper.ui.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import jone.helper.R;
import jone.helper.ui.activities.JoneAppManagerActivity;

/**
 * Created by jone.sun on 2015/8/17.
 */
public class MemoryManagerFragment extends BaseFragment<JoneAppManagerActivity> {
    private static final String TAG = MemoryManagerFragment.class.getSimpleName();
    ActivityManager activityManager;
    private TextView txt_memory;

    private static MemoryManagerFragment instance = null;
    public static MemoryManagerFragment getInstance(){
        if(instance == null){
            instance = new MemoryManagerFragment();
        }
        return instance;
    }
    public MemoryManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManager = (ActivityManager) getHostActivity().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_memory_manager;
    }

    @Override
    protected void findViews(View view) {
        txt_memory = findView(view, R.id.txt_memory);
    }

    @Override
    protected void initViews(View view) {

    }

    MyTask myTask = null;
    @Override
    public void onResume() {
        super.onResume();
        if(myTask == null || myTask.getStatus().equals(AsyncTask.Status.FINISHED.toString())){
            myTask = new MyTask();
            myTask.execute();
        }

    }

    private class MyTask extends AsyncTask<Void, Void, StringBuffer>{

        @Override
        protected StringBuffer doInBackground(Void... params) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);
            String availMemStr = Formatter.formatFileSize(getHostActivity(), mi.availMem);
            String totalMemStr = Formatter.formatFileSize(getHostActivity(), getTotalMem(activityManager));

            StringBuffer buffer = new StringBuffer("可用内存: " + availMemStr + "/" + totalMemStr + "\r\n");
            buffer.append("临界值: " + Formatter.formatFileSize(getHostActivity(), mi.threshold) + " " + mi.lowMemory).append("\r\n");
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList){
                buffer.append("\r\n")
                        .append("uid: " + runningAppProcessInfo.uid + "\r\n"
                                + "processName: " + runningAppProcessInfo.processName + "\r\n"
                                + "pid: " + runningAppProcessInfo.pid)
                        .append("\r\n");
                String[] pkgList = runningAppProcessInfo.pkgList;
                for(int i = 0; i < pkgList.length; i++){
                    buffer.append("pkg_" + i + ": " + pkgList[i]).append("\r\n");
                }
                // 获得该进程占用的内存
                int[] myMempid = new int[] { runningAppProcessInfo.pid };
                // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                Debug.MemoryInfo[] memoryInfo = activityManager
                        .getProcessMemoryInfo(myMempid);
                // 获取进程占内存用信息 kb单位
                int memSize = memoryInfo[0].dalvikPrivateDirty;
                buffer.append("占用内存: " + memSize + "kb");
                buffer.append("\r\n").append("\r\n");
            }
            return buffer;
        }

        @Override
        protected void onPostExecute(StringBuffer stringBuffer) {
            super.onPostExecute(stringBuffer);
            txt_memory.setText(stringBuffer.toString());
        }
    }

    /**
     * 获取手机的总内存
     * @return
     */
    public static long getTotalMem(ActivityManager activityManager){
        if(Build.VERSION.SDK_INT >= 16){
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);
            return mi.totalMem;
        }else {
            try {
                File file = new File("/proc/meminfo");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String totalRam = br.readLine();
                StringBuffer sb = new StringBuffer();
                char[] cs = totalRam.toCharArray();
                for (char c : cs) {
                    if(c>='0' && c<='9'){
                        sb.append(c);
                    }
                }
                long result = Long.parseLong(sb.toString())*1024;
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

    }
}
