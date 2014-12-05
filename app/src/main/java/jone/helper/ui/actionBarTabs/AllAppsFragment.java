package jone.helper.ui.actionBarTabs;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jone.helper.R;
import jone.helper.asyncTaskLoader.CustomListAsyncTaskLoader;
import jone.helper.callbacks.CommonListener;


/**
 * Created by jone_admin on 14-1-21.
 */
public class AllAppsFragment extends Fragment {
    private static final String TAG = AllAppsFragment.class.getSimpleName();
    private static final int APP_LIST_LOADER = 00001;
    private View rootView;
    private PackageManager packageManager;
    private GridView gridView_appList;
    private List<Map<String, Object>> appItems = new ArrayList<Map<String, Object>>();
    private List<ApplicationInfo> applicationInfoList = new ArrayList<ApplicationInfo>();
    private SimpleAdapter adapter;
    private LoaderManager loaderManager;
    private TextView txt_info;
    private ProgressBar apps_progressBar;
    private BroadcastReceiver appChangeBroadcastReceiver;
    public AllAppsFragment() {
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
        gridView_appList = (GridView) rootView.findViewById(R.id.gridView_appList);
        txt_info = setGridEmptyView(getActivity(), gridView_appList, "加载中...");
        adapter = new SimpleAdapter(getActivity(),
                appItems,
                R.layout.item_app_list,
                new String[]{"app_icon", "app_title"},
                new int[]{R.id.app_icon, R.id.app_title});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Drawable){
                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)o);
                    return true;
                }
                else return false;
            }
        });
        gridView_appList.setAdapter(adapter);
        gridView_appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ApplicationInfo applicationInfo = applicationInfoList.get(i);
                try{
                    Intent intent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getActivity(), applicationInfo.loadLabel(packageManager) + "没有主启动项", Toast.LENGTH_LONG).show();
                }
            }
        });
        gridView_appList.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView_appList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

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
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        loaderManager.initLoader(APP_LIST_LOADER, null, appsCallbacks);
    }

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED); //一个新应用包已经安装在设备上，数据包括包名（最新安装的包程序不能接收到这个广播）
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED); //一个新版本的应用安装到设备，替换之前已经存在的版本
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED); //一个已存在的应用程序包已经从设备上移除，包括包名（正在被安装的包程序不能接收到这个广播）
        appChangeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "app列表变化");
            }
        };
        getActivity().registerReceiver(appChangeBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loaderManager != null && loaderManager.getLoader(APP_LIST_LOADER) != null){
            loaderManager.destroyLoader(APP_LIST_LOADER);
        }
    }

    private LoaderManager.LoaderCallbacks<List> appsCallbacks = new LoaderManager.LoaderCallbacks<List>() {

        @Override
        public Loader<List> onCreateLoader(int i, final Bundle bundle) {
            return new CustomListAsyncTaskLoader(getActivity(), new CustomListAsyncTaskLoader.LoadListener() {
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
            }
            appItems.clear();
            List<ApplicationInfo> applicationInfoList = list;
            for(ApplicationInfo applicationInfo : applicationInfoList){
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("app_icon", applicationInfo.loadIcon(packageManager));
                item.put("app_title", applicationInfo.loadLabel(packageManager));//按序号添加ItemText
                item.put("applicationInfo", applicationInfo);
                appItems.add(item);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List> listLoader) {
            appItems.clear();
        }
    };

    private List<ApplicationInfo> getAppList(){
        applicationInfoList =  new ArrayList<ApplicationInfo>();
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

}
