package jone.helper.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jone.helper.R;
import jone.helper.ui.adapter.AppsAdapter;
import jone.helper.ui.loader.CustomV4ListAsyncTaskLoader;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.view.PullToRefreshView;

public class AllAppsFragment extends Fragment {
    private static final String TAG = AllAppsFragment.class.getSimpleName();
    private static final int APP_LIST_LOADER = 10001;
    private View rootView;
    private PackageManager packageManager;
    private AppsAdapter adapter;
    private LoaderManager loaderManager;
    private TextView txt_info;
    private ProgressBar apps_progressBar;
    private PullToRefreshView pullToRefreshView;
    private BroadcastReceiver appChangeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "app列表变化");
            if(loaderManager != null){
                if(loaderManager.getLoader(APP_LIST_LOADER) == null){
                    loaderManager.initLoader(APP_LIST_LOADER, null, appsCallbacks);
                }else {
                    loaderManager.restartLoader(APP_LIST_LOADER, null, appsCallbacks);
                }
            }
        }
    };
    private static AllAppsFragment instance = null;
    public static AllAppsFragment getInstance(){
        if(instance == null){
            instance = new AllAppsFragment();
        }
        return instance;
    }
    public AllAppsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindBroadcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_apps, container, false);
        initViews();
        return rootView;
    }
    private void initViews(){
        packageManager = getActivity().getPackageManager();
        loaderManager = getLoaderManager();
        apps_progressBar = (ProgressBar) rootView.findViewById(R.id.apps_progressBar);
        GridView gridView_appList = (GridView) rootView.findViewById(R.id.gridView_appList);
        txt_info = setGridEmptyView(getActivity(), gridView_appList, "加载中...");
        adapter = new AppsAdapter(getActivity());
        gridView_appList.setAdapter(adapter);
        gridView_appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ApplicationInfo applicationInfo = (ApplicationInfo) adapterView.getAdapter().getItem(i);
                try {
                    Intent intent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), applicationInfo.loadLabel(packageManager) + "没有主启动项", Toast.LENGTH_LONG).show();
                }
            }
        });
        gridView_appList.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView_appList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                adapter.checkSelect(actionMode, i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_app_list_operater, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        List<ApplicationInfo> applicationInfos = adapter.getSelectDeleteData();
                        if (applicationInfos != null && applicationInfos.size() > 0) {
                            ApplicationInfo applicationInfo = applicationInfos.get(0);
                            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                                //系统应用
                                Toast.makeText(getActivity(), "系统应用不可卸载", Toast.LENGTH_SHORT).show();
                            } else {
                                //用户应用
                                SystemUtil.uninstallAPK(getActivity(), applicationInfos.get(0).packageName);
                            }
                        }
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                adapter.unSelectAll(actionMode);
            }
        });

        pullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pullToRefreshView);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loaderManager.restartLoader(APP_LIST_LOADER, null, appsCallbacks);
            }
        });
        pullToRefreshView.setEnablePullLoadMoreDataStatus(false);

        loaderManager.initLoader(APP_LIST_LOADER, null, appsCallbacks);

    }

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(appChangeBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loaderManager != null && loaderManager.getLoader(APP_LIST_LOADER) != null){
            loaderManager.destroyLoader(APP_LIST_LOADER);
        }
        if(getActivity() != null){
            getActivity().unregisterReceiver(appChangeBroadcastReceiver);
        }
    }

    private LoaderManager.LoaderCallbacks<List> appsCallbacks = new LoaderManager.LoaderCallbacks<List>() {

        @Override
        public Loader<List> onCreateLoader(int i, final Bundle bundle) {
            return new CustomV4ListAsyncTaskLoader(getActivity(), new CustomV4ListAsyncTaskLoader.LoadListener() {
                @Override
                public List loading() {
                    return getAppList();
                }
            });
        }

        @Override
        public void onLoadFinished(Loader<List> listLoader, List list) {
            apps_progressBar.setVisibility(View.GONE);
            if(list.size() == 0){
                txt_info.setText("无应用");
            }else {
                adapter.setData(list);
            }
            if(pullToRefreshView != null){
                pullToRefreshView.onHeaderRefreshComplete();
            }
        }

        @Override
        public void onLoaderReset(Loader<List> listLoader) {
            adapter.clear();
        }
    };

    private List<ApplicationInfo> getAppList(){
        List<ApplicationInfo> applicationInfoList =  new ArrayList<>();
        List<ApplicationInfo> tmpList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo applicationInfo : tmpList){
            if(packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null){
                applicationInfoList.add(applicationInfo);
            }
        }
        //符合上面条件的全部查出来,并且排序
        Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(packageManager));
        return applicationInfoList;
    }

    public static TextView setGridEmptyView(Context context, GridView gridView, String info){
        TextView emptyView = getEmptyView(context, info);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)gridView.getParent()).addView(emptyView);
        gridView.setEmptyView(emptyView);
        return emptyView;
    }
    public static TextView getEmptyView(Context context, String info){
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setPadding(20, 20, 20, 20);
        emptyView.setTextSize(16);
        emptyView.setText(info);
        emptyView.setTextColor(context.getResources().getColor(android.R.color.white));
        return emptyView;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }
}
