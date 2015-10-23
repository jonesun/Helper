package jone.helper.model;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import jone.helper.ui.widget.circleprogress.ArcProgress;
import jone.helper.util.AppUtil;
import jone.helper.util.StorageUtil;

/**
 * Created by jone.sun on 2015/10/22.
 */
public class MemoryStoreProgressTool {

    private Timer timerMemory;
    private Timer timerStore;

    public void showMemoryProgress(final Activity activity, final ArcProgress arcProcess){
        timerMemory = new Timer();

        long l = AppUtil.getAvailMemory(activity);
        long y = AppUtil.getTotalMemory(activity);
        final double x = (((y - l) / (double) y) * 100);
        //   arcProcess.setProgress((int) x);

        arcProcess.setProgress(0);
        timerMemory.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arcProcess.getProgress() >= (int) x) {
                            timerMemory.cancel();
                        } else {
                            arcProcess.setProgress(arcProcess.getProgress() + 1);
                        }
                    }
                });
            }
        }, 50, 20);
    }

    public void showStoreProgress(final Activity activity, final ArcProgress arcStore, TextView txtCapacity){
        timerStore = new Timer();
        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(activity);

        long nAvailaBlock;
        long TotalBlocks;
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }

        final double percentStore = (((TotalBlocks - nAvailaBlock) / (double) TotalBlocks) * 100);
        txtCapacity.setText(StorageUtil.convertStorage(TotalBlocks - nAvailaBlock) + "/" + StorageUtil.convertStorage(TotalBlocks));
        arcStore.setProgress(0);

        timerStore.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arcStore.getProgress() >= (int) percentStore) {
                            timerStore.cancel();
                        } else {
                            arcStore.setProgress(arcStore.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);
    }

    public void close(){
        if(timerMemory != null){
            timerMemory.cancel();
        }
        if(timerStore != null){
            timerStore.cancel();
        }
    }
}
