package jone.helper.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

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
