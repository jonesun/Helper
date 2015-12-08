package jone.helper.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.AppProcessInfo;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.adapter.BaseRecyclerViewAdapter;
import jone.helper.ui.fragments.base.BaseFragment;
import jone.helper.util.AppUtil;
import jone.helper.util.asyncTaskLoader.CustomV4ListAsyncTaskLoader;

/**
 * Created by jone.sun on 2015/11/16.
 */
public class MemoryCleanFragment extends BaseFragment<JoneAppManagerActivity> implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MemoryCleanFragment.class.getSimpleName();
    public List<AppProcessInfo> data = new ArrayList<>();

    private static final int APP_LIST_LOADER = 10001;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progress_bar;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    private Button btn_clean;
    private LoaderManager loaderManager;

    public static MemoryCleanFragment newInstance() {
        return new MemoryCleanFragment();
    }

    public MemoryCleanFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_memory_clean;
    }

    @Override
    protected void findViews(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
        btn_clean = (Button) view.findViewById(R.id.btn_clean);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // 刷新时，指示器旋转后变化的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        myAdapter = new MyAdapter(getActivity());
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
        loaderManager = getLoaderManager();
        loaderManager.initLoader(APP_LIST_LOADER, null, appsCallbacks);

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                List<AppProcessInfo> appProcessInfos = myAdapter.getDataList();
                if(appProcessInfos != null && appProcessInfos.size() > 0){
                    for(AppProcessInfo appProcessInfo : appProcessInfos){
                        if(appProcessInfo.checked){
//                            Log.e(TAG, "清理: " + appProcessInfo.appName);
                            AppUtil.killBackgroundProcesses(getHostActivity(), appProcessInfo.processName);
                        }
                    }
                }
                progress_bar.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClean", true);
                loaderManager.restartLoader(APP_LIST_LOADER, bundle, appsCallbacks);
            }
        });
    }

    @Override
    public void onRefresh() {
        loaderManager.restartLoader(APP_LIST_LOADER, null, appsCallbacks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loaderManager != null && loaderManager.getLoader(APP_LIST_LOADER) != null){
            loaderManager.destroyLoader(APP_LIST_LOADER);
        }
    }

    private LoaderManager.LoaderCallbacks<List> appsCallbacks = new LoaderManager.LoaderCallbacks<List>() {
        private boolean isClean;
        @Override
        public Loader<List> onCreateLoader(int i, final Bundle bundle) {
            if(bundle != null && bundle.containsKey("isClean")){
                isClean = bundle.getBoolean("isClean");
            }else {
                isClean = false;
            }
            progress_bar.setVisibility(View.VISIBLE);
            return new CustomV4ListAsyncTaskLoader(getActivity(), new CustomV4ListAsyncTaskLoader.LoadListener() {
                @Override
                public List loading() {
                    return AppUtil.getRunningAppProcesses(getHostActivity());
                }
            });
        }

        @Override
        public void onLoadFinished(Loader<List> listLoader, List list) {
            if(isClean){
                App.showToast("清理完成");
            }
            myAdapter.setDataList(list);
            swipeRefreshLayout.setRefreshing(false);
            progress_bar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<List> listLoader) {

        }
    };

    private static class MyAdapter extends BaseRecyclerViewAdapter<MyAdapter.ViewHolder, AppProcessInfo> {

        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory_clean, parent, false);
            MyAdapter.ViewHolder mViewHolder = new MyAdapter.ViewHolder(mView);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            final AppProcessInfo appProcessInfo = getItem(position);
            if(appProcessInfo.isSystem){
                holder.text_name.setText(appProcessInfo.appName + "(系统)");
            }else {
                holder.text_name.setText(appProcessInfo.appName);
            }

            if(appProcessInfo.icon != null){
                holder.iv_icon.setImageDrawable(appProcessInfo.icon);
            }
            holder.tv_size.setText(Formatter.formatFileSize(getContext(), appProcessInfo.memory));
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    appProcessInfo.checked = isChecked;
                }
            });
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text_name, tv_size;
            private ImageView iv_icon;
            private CheckBox checkbox;
            public ViewHolder(View itemView) {
                super(itemView);
                text_name = (TextView) itemView.findViewById(R.id.text_name);
                iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
                tv_size = (TextView) itemView.findViewById(R.id.tv_size);
                checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
        }
    }
}
