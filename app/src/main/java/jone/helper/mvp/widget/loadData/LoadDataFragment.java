package jone.helper.mvp.widget.loadData;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.util.SystemUtil;
import jone.helper.mvp.presenter.loadData.LoadDataPresenter;
import jone.helper.mvp.view.loadData.DefaultLoadMoreView;
import jone.helper.mvp.view.loadData.LoadDataView;
import jone.helper.mvp.view.loadData.LoadMoreView;

/**
 * 加载数据Fragment
 * Created by jone.sun on 2016/1/14.
 */
public abstract class LoadDataFragment<T> extends Fragment implements LoadDataView<T>,
        SwipeRefreshLayout.OnRefreshListener, LoadDataRecyclerViewAdapter.OnItemClickListener<T>{
    private static final String TAG = "LoadDataFragment";
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private TextView txtEmpty;
    private LinearLayout layout_no_network;
    private ProgressBar progress_bar;
    private LinearLayoutManager mLayoutManager;
    private LoadDataRecyclerViewAdapter<T> adapter;
    private LoadDataPresenter loadDataPresenter;
    private LoadMoreView loadMoreView;
    private boolean hasRootView = false;
    private boolean isLoading = false;
    private boolean isLoadMore = false;

    public abstract LoadDataPresenter initLoadDataPresenter();

    public abstract LoadDataRecyclerViewAdapter<T> initAdapter(LoadMoreView loadMoreView);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDataPresenter = initLoadDataPresenter();
    }

    /**
     * 是否检查网络
     * @return
     */
    public boolean checkNetwork(){
        return true;
    }

    @Override @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_load_data, container, false);
            hasRootView = false;
        } else {
            hasRootView = true;
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!hasRootView){
            mSwipeRefreshWidget = findViewWithGeneric(view, R.id.swipe_refresh_widget);
            mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
            mSwipeRefreshWidget.setOnRefreshListener(this);
            // 这句话是为了，第一次进入页面的时候显示加载进度条
//            mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
//                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                            .getDisplayMetrics()));
            txtEmpty = findViewWithGeneric(view, android.R.id.empty);
            progress_bar = findViewWithGeneric(view, R.id.progress_bar);
            mRecyclerView = findViewWithGeneric(view, android.R.id.list);

            initRecyclerView(mRecyclerView);
            loadMoreView = initLoadMoreView();
            adapter = initAdapter(loadMoreView);
            mLayoutManager = initLayoutManager();

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(new RecyclerOnScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore() {
                    if(adapter.isShowFooter()){
                        if(!isLoading){
                            isLoadMore = true;
                            loadDataPresenter.loadMore();
                        }else {
                            Log.e(TAG, "onLoadMore>>正在加载中...");
                        }
                    }
                }
            });
            adapter.setOnItemClickListener(this);
            if(SystemUtil.hasNetWork(getContext()) || !checkNetwork()){
                onRefresh();
            }else {
                mSwipeRefreshWidget.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                layout_no_network = (LinearLayout)((ViewStub) view.findViewById(R.id.stub_no_network)).inflate();
                TextView tvRefresh = (TextView) layout_no_network.findViewById(R.id.tv_refresh);
                tvRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(SystemUtil.hasNetWork(getActivity())){
                            if(layout_no_network != null){
                                layout_no_network.setVisibility(View.GONE);
                                layout_no_network = null;
                            }
                            mSwipeRefreshWidget.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            onRefresh();
                        }else {
                            App.showToast(getString(R.string.toast_please_check_network));
                        }
                    }
                });
            }
        }
    }

    public <T extends View> T findViewWithGeneric(View view, int id) {
        return (T) view.findViewById(id);
    }

    /***
     * 外部可修改LinearLayoutManager GridLayoutManager
     * @return
     */
    public LinearLayoutManager initLayoutManager(){
        return new LinearLayoutManager(getActivity());
    }

    /***
     * 外部可扩展RecyclerView
     * @param mRecyclerView
     */
    public void initRecyclerView(RecyclerView mRecyclerView){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onRefresh() {
        if(!isLoading){
            isLoadMore = false;
            adapter.setShowFooter(true);
            loadMoreView.showNormal();
            loadDataPresenter.refresh();
        }else {
            Log.e(TAG, "onRefresh>>正在加载中...");
        }
    }

    @Override
    public void showProgress() {
        txtEmpty.setVisibility(View.GONE);
        isLoading = true;
        if(isLoadMore){
            loadMoreView.showLoading();
        }else {
            mSwipeRefreshWidget.setRefreshing(true);
            progress_bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        isLoading = false;
        mSwipeRefreshWidget.setRefreshing(false);
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void addDataList(List<T> dataList) {
        if(isLoadMore){
            adapter.addDataList(dataList);
            loadMoreView.showNormal();
        }else {
            adapter.setDataList(dataList);
        }
    }


    @Override
    public void showLoadFailMsg(@Reason int reason, String message) {
        if(reason == REASON_OF_NO_NEXT){
            adapter.setShowFooter(false);
            loadMoreView.showNomore();
            return;
        }
        if(isLoadMore){
            if(reason == REASON_OF_NO_DATA){
                adapter.setShowFooter(false);
                loadMoreView.showNomore();
            }else {
                loadMoreView.showFail();
            }
        }else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        String msg = getString(R.string.load_fail);
        switch (reason){
            case REASON_OF_NO_DATA:
                msg = getString(R.string.no_data);
                break;
            case REASON_OF_NO_NETWORK:
                msg = getString(R.string.no_network);
                break;
            case REASON_OF_SERVER_ERROR:
                msg = getString(R.string.server_error);
                break;
        }
        Log.e(TAG, "showLoadFailMsg>>reason: " + reason + " message: " + message);
        Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public LoadMoreView initLoadMoreView(){
        return new DefaultLoadMoreView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadDataPresenter != null){
                    loadDataPresenter.loadMore();
                }
            }
        });
    }

    public LoadDataRecyclerViewAdapter<T> getAdapter() {
        return adapter;
    }

    public LoadDataPresenter getLoadDataPresenter() {
        return loadDataPresenter;
    }
}
