package jone.helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jone.sun on 2015/12/1.
 */
public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    Log.e(TAG, "收到消息" + msg.what + bundle.getString("name", ""));
                    //获取客户端message中的Messenger，用于回调
                    Messenger messenger = msg.replyTo;
                    try {
                        // 回调
                        messenger.send(Message.obtain(null, 0));
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(handler).getBinder();
    }
}
