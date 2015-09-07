package jone.helper.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.zxing.common.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import jone.helper.R;

/**
 * Created by jone.sun on 2015/3/6.
 */
public class NewsDetailActivity extends Activity {

    private WebView webView;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().hasExtra("url")){
            webView.loadUrl(getIntent().getStringExtra("url"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        webView = (WebView) findViewById(R.id.webView);
        if(getIntent().hasExtra("url")){
            webView.loadUrl(getIntent().getStringExtra("url"));
        }
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        Toast.makeText(NewsDetailActivity.this, "努力加载中...", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
