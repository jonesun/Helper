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

import java.util.ArrayList;

import jone.helper.model.bing.BingPicture;
import jone.helper.model.bing.BingPictureOperator;
import jone.helper.model.bing.OnBingPicturesListener;
import jone.helper.ui.activities.HelperMainActivity;

/**
 * Created by jone.sun on 2015/12/1.
 */
public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    public static final int WHAT_REQUEST_PICTURE_LIST = 10001;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_REQUEST_PICTURE_LIST:
                    Bundle bundle = msg.getData();
                    Log.e(TAG, "收到消息" + msg.what + bundle.getString("name", ""));
                    getPictureUrls(msg.replyTo);
                    break;
                case 1:
                    Bundle bundle1 = msg.getData();
                    Log.e(TAG, "收到消息" + msg.what + bundle1.getString("name", ""));
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

    private void getPictureUrls(final Messenger messenger){
        BingPictureOperator.getInstance().getPictureUrls(4, new OnBingPicturesListener() {
            @Override
            public void onSuccess(ArrayList<BingPicture> bingPictureList) {
                try {
                    // 回调
                    Message message = Message.obtain(null, HelperMainActivity.WHAT_RESPONSE_PICTURE_LIST);
                    Bundle bundle = new Bundle();
//                    BingPictureMsg bingPictureMsg = new BingPictureMsg();
//                    bingPictureMsg.setTitle("title");
//                    bingPictureMsg.setLink("link");
//                    bingPictureMsg.setText("text");
//                    bundle.putParcelable("data", bingPictureMsg);
                    bundle.putParcelableArrayList("bingPictureList", bingPictureList);
                    bundle.putString("name", "我是" + TAG);
                    message.setData(bundle);
                    messenger.send(message);
                }catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String reason) {
                try {
                    // 回调
                    messenger.send(Message.obtain(null, HelperMainActivity.WHAT_RESPONSE_PICTURE_LIST));
                }catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
