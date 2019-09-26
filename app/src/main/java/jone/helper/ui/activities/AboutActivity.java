package jone.helper.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import jone.helper.BuildConfig;
import jone.helper.R;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.Utils;
import jone.helper.opengl.HomeGLRenderer;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.util.SharedToUtil;

/**
 * Created by jone.sun on 2015/9/10.
 */
public class AboutActivity extends BaseAppCompatActivity {
    private GLSurfaceView glsurfaceview;
    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    protected void findViews() {
        initToolbar();
        glsurfaceview = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        HomeGLRenderer homeGLRenderer = new HomeGLRenderer(AboutActivity.this);
        glsurfaceview.setZOrderOnTop(true);
        glsurfaceview.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glsurfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glsurfaceview.setRenderer(homeGLRenderer);
        TextView txtAppVersion = (TextView) findViewById(R.id.txtAppVersion);
        txtAppVersion.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

        findViewById(R.id.layoutAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, GuideActivity.class));
            }
        });

        findViewById(R.id.layoutShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedToUtil.shareToWeixin(AboutActivity.this,
                            "欢迎来到帮手的世界\nhttp://shouji.baidu.com/software/item?docid=7577214&from=as",
                            Utils.getSharedPicFile(AboutActivity.this));
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.layoutContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ContactOurDialog().show(getFragmentManager(), "contactOur");
            }
        });

        findViewById(R.id.layoutUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUpdateAgent.setDefault();
                UmengUpdateAgent.forceUpdate(AboutActivity.this);
                UmengUpdateAgent.setUpdateAutoPopup(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                        Log.e("sss", "i: " + updateStatus + " updateResponse：" + GsonUtils.toJson(updateResponse));
                        switch (updateStatus) {
                            case UpdateStatus.Yes: // has update
                                UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateResponse);
                                break;
                            case UpdateStatus.No: // has no update
                                Toast.makeText(AboutActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                Toast.makeText(AboutActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout: // time out
                                Toast.makeText(AboutActivity.this, "超时", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });

        findViewById(R.id.layoutFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedbackAgent fb = new FeedbackAgent(AboutActivity.this);
                fb.startFeedbackActivity();
            }
        });
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        glsurfaceview.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        glsurfaceview.onResume();
    }

    public static class ContactOurDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_contact_our, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            return builder.create();
        }
    }
}
