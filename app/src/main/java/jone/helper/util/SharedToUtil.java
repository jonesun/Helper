package jone.helper.util;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class SharedToUtil {
    private static final String PACKAGE_NAME_WEI_XIN = "com.tencent.mm";
    private static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";

    /***
     * 判断是否安装了某应用
     * @param context
     * @param pkgName
     * @return
     */
    @SuppressWarnings("finally")
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            //e.printStackTrace();
        } finally{
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        }

    }

    public static void shareToWeixin(Activity activity, String txt, File... files) {
        if(isPkgInstalled(activity, PACKAGE_NAME_WEI_XIN)){
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(txt != null){
                intent.putExtra("Kdescription", txt);
            }
            if(files != null){
                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                for (File f : files) {
                    imageUris.add(Uri.fromFile(f));
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }
            activity.startActivity(intent);
        }else {
            new AlertDialog.Builder(activity)
                    .setMessage("您尚未安装微信")
                    .setNegativeButton(android.R.string.ok, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO 安装微信
                        }
                    }).create().show();
        }

    }
}
