package jone.helper.lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


/**
 * 公共库Application
 * Created by jone.sun on 2015/12/8.
 */
public class PublicLibraryApp extends Application{
    public static final int WHAT_SHOW_TOAST = 1010101;
    private static final String TAG = "PublicLibraryApp";
    private static PublicLibraryApp instance;
    private static Handler handler = new Handler();
    public static PublicLibraryApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new ShowToastHandler(instance);

//        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                //Log.e(TAG, "onActivityCreated: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//                Log.d(TAG, "onActivityStarted: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//                Log.d(TAG, "onActivityResumed: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//                Log.d(TAG, "onActivityPaused: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//                Log.d(TAG, "onActivityStopped: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                Log.d(TAG, "onActivitySaveInstanceState: " + activity.getComponentName());
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//                Log.d(TAG, "onActivityDestroyed: " + activity.getComponentName());
//            }
//        });
    }

    static class ShowToastHandler extends Handler {
        private Context context;

        public ShowToastHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SHOW_TOAST:
                    if (msg.obj != null) {
                        String info = msg.obj.toString();
                        if (msg.arg1 == Toast.LENGTH_SHORT) {
                            Toast.makeText(context, info, Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(context, info, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static Handler getHandler() {
        return handler;
    }

    public static void showToast(String info) {
        showToast(info, Toast.LENGTH_SHORT);
    }

    public static void showToast(String info, int toastTime) {
        handler.sendMessage(handler.obtainMessage(WHAT_SHOW_TOAST, toastTime, 0, info));
    }
}
