package jone.helper.ui.fragments.base;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.ui.adapter.BaseRecyclerViewAdapter;
import jone.helper.util.asyncTaskLoader.CustomV4ListAsyncTaskLoader;

/**
 * SwipeRefreshLayout里面需要注意的Api：
 * 1、setOnRefreshListener(OnRefreshListener listener)  设置下拉监听，当用户下拉的时候会去执行回调
 * 2、setColorSchemeColors(int... colors) 设置 进度条的颜色变化，最多可以设置4种颜色
 * 3、setProgressViewOffset(boolean scale, int start, int end) 调整进度条距离屏幕顶部的距离
 * 4、setRefreshing(boolean refreshing) 设置SwipeRefreshLayout当前是否处于刷新状态，一般是在请求数据的时候设置为true，在数据被加载到View中后，设置为false。
 * Created by jone.sun on 2015/12/14.
 */
public abstract class BaseRecyclerViewFragment<F extends FragmentActivity, H extends RecyclerView.ViewHolder, T> extends BaseFragment<F> {
    private static String DEBUG_TAG = BaseRecyclerViewFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshWidget;
    private LinearLayoutManager mLayoutManager;
    private BaseRecyclerViewAdapter<H, T> mAdapter;

    private LoaderManager loaderManager;
    private static final int LOAD_ID = 1001;

    private static final String LOAD_EVENT = "load_event"; //加载方式
    private static final int LOAD_EVENT_NORMAL = 0; //正常加载
    private static final int LOAD_EVENT_ON_REFRESH = 1; //下拉刷新加载
    private static final int LOAD_EVENT_ON_LOAD_MORE = 2; //上拉加载更多加载

    public static final int SHOW_MODE_NORMAL = 0; //正常模式
    public static final int SHOW_MODE_HAVE_REFRESH = 1; //含有下拉刷新
    public static final int SHOW_MODE_HAVE_REFRESH_AND_MORE = 2; //含有下拉刷新和上拉加载更多

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_MODE_NORMAL, SHOW_MODE_HAVE_REFRESH, SHOW_MODE_HAVE_REFRESH_AND_MORE})
    public @interface show_mode {}

    private int showMode = SHOW_MODE_NORMAL; //展示形式

    private int lastVisibleItem;

    private boolean enableDebugLogging = true; //是否允许显示log信息
    private boolean isLoading = false; //是否正在加载中

    private boolean isLoadEnd = false; //是否加载到最后一页

    @Override
    protected int getContentView() {
        if(showMode == SHOW_MODE_NORMAL){
            return R.layout.base_fragment_recycler_view_normal;
        }
        return R.layout.base_fragment_recycler_view;
    }

    @Override
    protected void findViews(View view) {
        //// TODO: 2015/12/14   无数据 无网络 无下一页

        loaderManager = getLoaderManager();
        if(showMode != SHOW_MODE_NORMAL){
            mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
            mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        }
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);

        if(mSwipeRefreshWidget != null){
            mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(enableDebugLogging){
                        Log.e(DEBUG_TAG, "下拉刷新...");
                    }
                    load(LOAD_EVENT_ON_REFRESH);
                }
            });

            // 这句话是为了，第一次进入页面的时候显示加载进度条
            mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                            .getDisplayMetrics()));
        }
        if(showMode == SHOW_MODE_HAVE_REFRESH_AND_MORE){
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView,
                                                 int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                        if(enableDebugLogging){
                            Log.e(DEBUG_TAG, "加载更多...");
                        }
                        load(LOAD_EVENT_ON_LOAD_MORE);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                }
            });
        }
        mLayoutManager = getLayoutManager();
        mAdapter = getAdapter();

        configRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        load(LOAD_EVENT_NORMAL);
    }

    public abstract BaseRecyclerViewAdapter<H, T> getAdapter();

    /***
     * 需设置是否有下一页的标志 {@link #setLoadEnd(boolean)}
     * @return
     */
    public abstract List<T> getList();

    /***
     * 列表展示模式(目前支持list和grid)
     *  LinearLayoutManager GridLayoutManager
     * @return
     */
    public LinearLayoutManager getLayoutManager(){
        return new LinearLayoutManager(getHostActivity());
    }

    /***
     * 配置RecyclerView
     * @param recyclerView
     */
    public void configRecyclerView(RecyclerView recyclerView){
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /***
     * 设置展示形式(名字？ 目前支持正常模式、带下拉刷新、带下拉刷新和上拉加载更多)
     * @param showMode
     */
    public void setShowMode(@show_mode int showMode){
        this.showMode = showMode;
    }

    /***
     * 是否允许输出debug log
     * @param enable
     */
    public void enableDebugLogging(boolean enable){
        this.enableDebugLogging = enable;
    }

    public void setDebugTag(@NonNull String tag){
        DEBUG_TAG = TextUtils.isEmpty(tag) ? DEBUG_TAG : tag;
    }

    /***
     * 设置加载到最后(没有下一页了)
     * @param isLoadEnd
     */
    public void setLoadEnd(boolean isLoadEnd){
        this.isLoadEnd = isLoadEnd;
    }

    /***
     * 正在加载中的提示(上次加载还未完成)
     */
    public void onLoading(){
        App.showToast("正在加载中，请稍后再试");
    }

    public void onLoadEnd(){
        App.showToast("没有更多了");
    }

    public boolean isLoading(){
        return isLoading;
    }

    private void load(int loadEvent){
        if(isLoading){
            onLoading();
            return;
        }else if(loadEvent == LOAD_EVENT_ON_LOAD_MORE && isLoadEnd){
            onLoadEnd();
            return;
        }else {
            isLoadEnd = false;
        }
        isLoading = true;
        if(loadEvent != LOAD_EVENT_ON_REFRESH && mSwipeRefreshWidget != null){
            mSwipeRefreshWidget.setRefreshing(true);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(LOAD_EVENT, loadEvent);
        if(loaderManager.getLoader(LOAD_ID) != null){
            loaderManager.restartLoader(LOAD_ID, bundle, loaderCallbacks);
        }else {
            loaderManager.initLoader(LOAD_ID, bundle, loaderCallbacks);
        }
    }

    private LoaderManager.LoaderCallbacks<List<T>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<T>>() {
        private int loadEvent;

        @Override
        public Loader<List<T>> onCreateLoader(int i, final Bundle bundle) {
            if(bundle != null){
                loadEvent = bundle.getInt(LOAD_EVENT, LOAD_EVENT_NORMAL);
            }
            return new CustomV4ListAsyncTaskLoader<>(getHostActivity(), new CustomV4ListAsyncTaskLoader.LoadListener<T>() {
                @Override
                public List<T> loading() {
                    return getList();
                }
            });
        }

        @Override
        public void onLoadFinished(Loader<List<T>> listLoader, List<T> list) {
            if(loadEvent == LOAD_EVENT_ON_LOAD_MORE){
                mAdapter.addDataList(list);
            }else {
                mAdapter.setDataList(list);
            }
            if(mSwipeRefreshWidget != null){
                mSwipeRefreshWidget.setRefreshing(false);
            }
            isLoading = false;
        }

        @Override
        public void onLoaderReset(Loader<List<T>> listLoader) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loaderManager != null && loaderManager.getLoader(LOAD_ID) != null){
            loaderManager.destroyLoader(LOAD_ID);
        }
    }
}
