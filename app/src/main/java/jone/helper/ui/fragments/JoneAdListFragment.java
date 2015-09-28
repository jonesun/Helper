package jone.helper.ui.fragments;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.adapter.CustomAdAdapter;
import jone.helper.model.customAd.entity.CustomAdInfo;
import jone.helper.presenter.customAd.CustomAdPresenter;
import jone.helper.presenter.customAd.impl.JoneWPCustomPresenter;
import jone.helper.model.customAd.JoneBaiduAd;
import jone.helper.ui.view.CustomAdView;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class JoneAdListFragment extends BaseFragment<HelperMainActivity> implements CustomAdView {
    private static final String TAG = JoneAdListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private CustomAdAdapter adapter;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private LinearLayout baidu_layout_ad;

    private CustomAdPresenter presenter;

    private List<CustomAdInfo> dateList = new ArrayList<>();

    private static JoneAdListFragment instance = null;
    public static JoneAdListFragment getInstance(){
        if(instance == null){
            instance = new JoneAdListFragment();
        }
        return instance;
    }
    public JoneAdListFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_jone_ad_list;
    }

    @Override
    protected void findViews(View view) {
        mRecyclerView = findView(view, R.id.recyclerView);
        progressBar = findView(view, R.id.progressBar);
        baidu_layout_ad = findView(view, R.id.baidu_layout_ad);
    }

    @Override
    protected void initViews(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        adapter = new CustomAdAdapter(getActivity(), dateList);
        mRecyclerView.setAdapter(adapter);

        presenter = new JoneWPCustomPresenter(getHostActivity(), this); //传入WeatherView
        progressBar.setVisibility(View.VISIBLE);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getHostActivity());
        if(!sharedPreferences.getBoolean("getAd", false)){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter.getCustomAdList(getHostActivity());
                    sharedPreferences.edit().putBoolean("getAd", true).commit();
                }
            }, 3000);
        }else {
            presenter.getCustomAdList(getHostActivity());
        }
        JoneBaiduAd.showBDBannerAd(getHostActivity(), baidu_layout_ad);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String reason) {
        //Do something
        Toast.makeText(getHostActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCustomAdInfoList(List<CustomAdInfo> customAdInfoList) {
        dateList = customAdInfoList;
        if(dateList != null && dateList.size() > 0){
            adapter.setDataList(dateList);
            adapter.notifyDataSetChanged();
        }
    }
}
