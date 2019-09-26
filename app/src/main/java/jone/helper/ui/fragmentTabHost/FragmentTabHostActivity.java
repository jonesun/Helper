package jone.helper.ui.fragmentTabHost;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentTabHost;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import jone.helper.R;

public class FragmentTabHostActivity extends AppCompatActivity implements
        TabHost.OnTabChangeListener {
    private FragmentTabHost fragmentTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_tab_host);

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            fragmentTabHost.getTabWidget().setShowDividers(0);
        }

        initTabs();

        fragmentTabHost.setCurrentTab(0);
        fragmentTabHost.setOnTabChangedListener(this);
    }

    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = fragmentTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(FragmentTabHostActivity.this);
                }
            });
            fragmentTabHost.addTab(tab, mainTab.getClz(), null);
        }
    }


    @Override
    public void onTabChanged(String tabId) {
        final int size = fragmentTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = fragmentTabHost.getTabWidget().getChildAt(i);
            if (i == fragmentTabHost.getCurrentTab()) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }
}
