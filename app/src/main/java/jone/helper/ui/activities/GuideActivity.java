package jone.helper.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import jone.helper.R;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.adapter.GuideFragmentPagerAdapter;

public class GuideActivity extends AppCompatActivity {
    GuideFragmentPagerAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mAdapter = new GuideFragmentPagerAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}