package jone.helper.lib.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

/**
 * 监听外部存储状态
 * Created by jone.sun on 2015/12/14.
 */
public class MonitorExternalStorageUtils {
    private Context context;
    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    public MonitorExternalStorageUtils(Context context){
        this.context = context;
    }

    private void updateExternalStorageState(HandleExternalStorageState handleExternalStorageState) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if(handleExternalStorageState != null){
            handleExternalStorageState.handle(mExternalStorageAvailable,
                    mExternalStorageWriteable);
        }
    }

    public void startWatchingExternalStorage(final HandleExternalStorageState handleExternalStorageState) {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("test", "Storage: " + intent.getData());
                updateExternalStorageState(handleExternalStorageState);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        context.registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState(handleExternalStorageState);
    }

    public void stopWatchingExternalStorage() {
        context.unregisterReceiver(mExternalStorageReceiver);
    }

    public interface HandleExternalStorageState{
        void handle(boolean externalStorageAvailable, boolean externalStorageWriteable);
    }
}
