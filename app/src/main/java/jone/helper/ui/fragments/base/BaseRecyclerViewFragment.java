package jone.helper.ui.fragments.base;

import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.adapter.BaseRecyclerViewAdapter;
import jone.helper.ui.loader.CustomV4ListAsyncTaskLoader;

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
    private TextView txtEmpty;
    private LinearLayout linearLayoutNoLoad;

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
    public @interface show_mode {
    }

    public static final int NO_LOAD_REASON_WITH_FIRST_OF_NO_NETWORK = 0; //首次不加载的原因-无网络
    public static final int NO_LOAD_REASON_WITH_FIRST_OF_NO = -1; //首次不加载的原因-不检查不加载
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_LOAD_REASON_WITH_FIRST_OF_NO_NETWORK, NO_LOAD_REASON_WITH_FIRST_OF_NO})
    public @interface no_load_reason_with_first {
    }

    private int showMode = SHOW_MODE_NORMAL; //展示形式

    private int startPageIndex = 0; //默认初始化页面起始位置
    private int currentPageIndex = startPageIndex; //当前加载页面位置

    private int lastVisibleItem;

    private int noLoadReasonWithFirst = NO_LOAD_REASON_WITH_FIRST_OF_NO_NETWORK;
    private boolean enableDebugLogging = true; //是否允许显示log信息
    private boolean isLoading = false; //是否正在加载中

    private boolean isLoadEnd = false; //是否加载到最后一页

    private String loadingHint; //加载中的提示
    private String noDataHint; //没有数据的提示
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
    }

    /***
     * 初始化设置（外部可修改）
     */
    public void initConfig(){
        setLoadingHint(getString(R.string.loading)); //设置加载中的提示
        setNoDataHint(getString(R.string.no_data)); //设置没有数据的提示
//        setNoLoadReasonWithFirst(NO_LOAD_REASON_WITH_FIRST_OF_NO); //设置不检测首次不加载
    }

    @Override
    protected int getContentView() {
        if (showMode == SHOW_MODE_NORMAL) {
            return R.layout.base_fragment_recycler_view_normal;
        }
        return R.layout.base_fragment_recycler_view;
    }

    @Override
    protected void findViews(View view) {
        loaderManager = getLoaderManager();
        if (showMode != SHOW_MODE_NORMAL) {
            mSwipeRefreshWidget = findView(view, R.id.swipe_refresh_widget);
            mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        }
        final RecyclerView mRecyclerView = findView(view, android.R.id.list);
        txtEmpty = findView(view, android.R.id.empty);
        if (mSwipeRefreshWidget != null) {
            mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (enableDebugLogging) {
                        Log.e(DEBUG_TAG, "下拉刷新...");
                    }
                    if(noLoadReasonWithFirst == NO_LOAD_REASON_WITH_FIRST_OF_NO_NETWORK){
                        if(SystemUtil.hasNetWork(getHostActivity())){
                            if(linearLayoutNoLoad != null){
                                linearLayoutNoLoad.setVisibility(View.GONE);
                                linearLayoutNoLoad = null;
                            }
                            load(LOAD_EVENT_ON_REFRESH);
                        }else {
                            mSwipeRefreshWidget.setRefreshing(false);
                            App.showToast(getString(R.string.toast_please_check_network));
                        }
                    }else {
                        load(LOAD_EVENT_ON_REFRESH);
                    }
                }
            });

//            // 这句话是为了，第一次进入页面的时候显示加载进度条
//            mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
//                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                            .getDisplayMetrics()));
        }
        if (showMode == SHOW_MODE_HAVE_REFRESH_AND_MORE) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView,
                                                 int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                        if (enableDebugLogging) {
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
        if(noLoadReasonWithFirst == NO_LOAD_REASON_WITH_FIRST_OF_NO_NETWORK){
            if(!SystemUtil.hasNetWork(getHostActivity())){
                linearLayoutNoLoad = (LinearLayout)((ViewStub) view.findViewById(R.id.stub_no_network)).inflate();
                TextView tvRefresh = (TextView) linearLayoutNoLoad.findViewById(R.id.tv_refresh);
                tvRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(SystemUtil.hasNetWork(getActivity())){
                            if(linearLayoutNoLoad != null){
                                linearLayoutNoLoad.setVisibility(View.GONE);
                                linearLayoutNoLoad = null;
                            }
                            load(LOAD_EVENT_NORMAL);
                        }else {
                            App.showToast(getString(R.string.toast_please_check_network));
                        }
                    }
                });
            }else {
                load(LOAD_EVENT_NORMAL);
            }
        }else {
            load(LOAD_EVENT_NORMAL);
        }
    }

    /***
     * 加载数据
     *
     * @param loadEvent
     */
    private void load(int loadEvent) {
        if (enableDebugLogging) {
            Log.e(DEBUG_TAG, "load: " + loadEvent);
        }
        if (isLoading) {
            onLoading();
            return;
        } else if (loadEvent == LOAD_EVENT_ON_LOAD_MORE && isLoadEnd) {
            onLoadEnd();
            return;
        } else {
            isLoadEnd = false;
        }
        if(txtEmpty != null && !TextUtils.isEmpty(loadingHint) && loadEvent == LOAD_EVENT_NORMAL){
            txtEmpty.setText(loadingHint);
            txtEmpty.setVisibility(View.VISIBLE);
        }
        isLoading = true;
        if (loadEvent != LOAD_EVENT_ON_REFRESH && mSwipeRefreshWidget != null) {
            mSwipeRefreshWidget.setRefreshing(true);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(LOAD_EVENT, loadEvent);
        if (loaderManager.getLoader(LOAD_ID) != null) {
            loaderManager.restartLoader(LOAD_ID, bundle, loaderCallbacks);
        } else {
            /***我们通常在Activity的onCreate()方法中或者在Fragment的onActivityCreated()方法中来初始化一个Loader。初始化一个loader我们需要调用LoaderManager的initLoader()函数。***/
            loaderManager.initLoader(LOAD_ID, bundle, loaderCallbacks);
        }
    }

    public abstract BaseRecyclerViewAdapter<H, T> getAdapter();

    /***
     *
     * 可通过{@link #getCurrentPageIndex()} 来获取当前应该加载的页数
     * 需设置是否有下一页的标志 {@link #setLoadEnd(boolean)}
     *
     * @return
     */
    public abstract List<T> getList();

    /***
     * 列表展示模式(目前支持list和grid)
     * LinearLayoutManager GridLayoutManager
     *
     * @return
     */
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getHostActivity());
    }

    /***
     * 配置RecyclerView
     *
     * @param recyclerView
     */
    public void configRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /***
     * 设置展示形式(名字？ 目前支持正常模式、带下拉刷新、带下拉刷新和上拉加载更多)
     *
     * @param showMode
     */
    public void setShowMode(@show_mode int showMode) {
        this.showMode = showMode;
    }

    /***
     * 是否允许输出debug log
     *
     * @param enable
     */
    public void enableDebugLogging(boolean enable) {
        this.enableDebugLogging = enable;
    }

    /***
     * 设置输出日志的TAG
     *
     * @param tag
     */
    public void setDebugTag(@NonNull String tag) {
        DEBUG_TAG = TextUtils.isEmpty(tag) ? DEBUG_TAG : tag;
    }

    /***
     * 设置起始页面位置(默认从0开始)
     * @param startPageIndex
     */
    public void setStartPageIndex(int startPageIndex) {
        this.startPageIndex = startPageIndex;
    }

    /***
     * 设置加载到最后(没有下一页了)
     *
     * @param isLoadEnd
     */
    public void setLoadEnd(boolean isLoadEnd) {
        this.isLoadEnd = isLoadEnd;
    }

    /***
     * 设置首次不加载数据的原因
     * @param noLoadReasonWithFirst
     */
    public void setNoLoadReasonWithFirst(@no_load_reason_with_first int noLoadReasonWithFirst) {
        this.noLoadReasonWithFirst = noLoadReasonWithFirst;
    }

    /***
     * 正在加载中的提示(上次加载还未完成)
     */
    public void onLoading() {
        App.showToast("正在加载中，请稍后再试");
    }

    /***
     * 没有更多数据的提示
     */
    public void onLoadEnd() {
        App.showToast("没有更多了");
    }

    /***
     * 判断是否正在加载中
     *
     * @return
     */
    public boolean isLoading() {
        return isLoading;
    }

    /***
     * 加载数据(供外部使用)
     */
    public void load() {
        load(LOAD_EVENT_ON_REFRESH);
    }


    private LoaderManager.LoaderCallbacks<List<T>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<T>>() {
        private int loadEvent;

        /***用给定的ID实例化并返回一个新的Loader对象***/
        /***
         * 当你想要访问一个装载器时(例如，通过initLoader()方法（initLoader(int ID, Bundle args, LoaderCallbacks<D> callbacks)）)，
         * 它会检查指定ID的装载器是否存在，如果不存在，
         * 它会触发 LoaderManager.LoaderCallbacks的onCreateLoader()回调方法。
         * 这是创建一个新的装载器的地方，典型的是创建 一个CursorLoader装载器，
         * 但是你能够实现你自己的Loader类的子类。
         * @param i
         * @param bundle
         * @return
         */
        @Override
        public Loader<List<T>> onCreateLoader(int i, final Bundle bundle) {
            if (bundle != null) {
                loadEvent = bundle.getInt(LOAD_EVENT, LOAD_EVENT_NORMAL);
            }
            setPageIndex(loadEvent);
            return new CustomV4ListAsyncTaskLoader<>(getHostActivity(), new CustomV4ListAsyncTaskLoader.LoadListener<T>() {
                @Override
                public List<T> loading() {
                    return getList();
                }
            });
        }

        /***当之前创建的装载器已经完成它的装载时，调用这个方法***/
        @Override
        public void onLoadFinished(Loader<List<T>> listLoader, List<T> list) {
            if (loadEvent == LOAD_EVENT_ON_LOAD_MORE) {
                mAdapter.addDataList(list);
            } else {
                mAdapter.setDataList(list);
                if (mAdapter.getItemCount() == 0 && txtEmpty != null && !TextUtils.isEmpty(noDataHint)) {
                    if (enableDebugLogging) {
                        Log.e(DEBUG_TAG, "无数据");
                    }
                    txtEmpty.setText(noDataHint);
                    txtEmpty.setVisibility(View.VISIBLE);
                } else if (txtEmpty != null && txtEmpty.getVisibility() == View.VISIBLE) {
                    txtEmpty.setVisibility(View.GONE);
                }
            }
            if (mSwipeRefreshWidget != null) {
                mSwipeRefreshWidget.setRefreshing(false);
            }
            isLoading = false;
        }

        /***当之前创建的装载器被重置时，调用这个方法，这样会使这个装载器的数据变的无效。
         * 重置loader的时候回调它，这样同时会让loader的所有数据不可得，
         * 为了保证这一点，应用程序应该移除所有对loader数据的引用
         * ***/
        @Override
        public void onLoaderReset(Loader<List<T>> listLoader) {

        }

        private void setPageIndex(int loadEvent){
            if (loadEvent != LOAD_EVENT_ON_LOAD_MORE) {
                currentPageIndex = startPageIndex;
            }else if(!isLoadEnd){
                currentPageIndex++;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loaderManager != null && loaderManager.getLoader(LOAD_ID) != null) {
            loaderManager.destroyLoader(LOAD_ID);
        }
    }

    public String getNoDataHint() {
        return noDataHint;
    }

    public void setNoDataHint(String noDataHint) {
        this.noDataHint = noDataHint;
    }

    public String getLoadingHint() {
        return loadingHint;
    }

    public void setLoadingHint(String loadingHint) {
        this.loadingHint = loadingHint;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }
}
