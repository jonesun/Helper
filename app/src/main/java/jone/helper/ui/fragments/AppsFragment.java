 package jone.helper.ui.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jone.helper.R;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.adapter.AppsRecyclerViewAdapter;
import jone.helper.util.asyncTaskLoader.CustomV4ListAsyncTaskLoader;

 public class AppsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AppsRecyclerViewAdapter.OnItemClickListener{
     private final String TAG = AppsFragment.class.getSimpleName();
     private static final int APP_LIST_LOADER = 10001;
     private View rootView;
     private SwipeRefreshLayout swipeRefreshLayout;
     private RecyclerView recyclerView;
     private AppsRecyclerViewAdapter appsRecyclerViewAdapter;
     private ProgressDialog progressDialog;

     private PackageManager packageManager;
     private LoaderManager loaderManager;
     public static AppsFragment newInstance() {
        return new AppsFragment();
     }

     public AppsFragment() {
        // Required empty public constructor
     }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getActivity().getPackageManager();
        loaderManager = getLoaderManager();
        registerBroadcast();
    }

     private void registerBroadcast(){
         IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
         intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
         intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
         intentFilter.addDataScheme("package");
         getActivity().registerReceiver(appChangeBroadcastReceiver, intentFilter);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_apps, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);

        // 刷新时，指示器旋转后变化的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        appsRecyclerViewAdapter = new AppsRecyclerViewAdapter(getActivity());
        appsRecyclerViewAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(appsRecyclerViewAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        progressDialog = ProgressDialog.show(getActivity(), "提示", "加载中");
        loaderManager.initLoader(APP_LIST_LOADER, null, appsCallbacks);
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
         if(getActivity() != null){
             getActivity().unregisterReceiver(appChangeBroadcastReceiver);
         }
     }


     @Override
     public void onItemClick(View view, int position) {
         ApplicationInfo applicationInfo = appsRecyclerViewAdapter.getItem(position);
         try {
             Intent intent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
             startActivity(intent);
         } catch (Exception e) {
             Toast.makeText(getActivity(), applicationInfo.loadLabel(packageManager) + "没有主启动项", Toast.LENGTH_LONG).show();
         }
     }

     @Override
     public void onItemLongClick(final View view, final int position) {
         view.setBackgroundColor(Color.RED);
         final ApplicationInfo applicationInfo = appsRecyclerViewAdapter.getItem(position);
         Snackbar snackbar = Snackbar.make(view, applicationInfo.loadLabel(packageManager), Snackbar.LENGTH_LONG)
                 .setAction("卸载", new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                             //系统应用
                             Toast.makeText(getActivity(), "系统应用不可卸载", Toast.LENGTH_SHORT).show();
                         } else {
                             //用户应用
                             SystemUtil.uninstallAPK(getActivity(), applicationInfo.packageName);
                         }
                     }
                 });
         snackbar.setCallback(new Snackbar.Callback() {
             @Override
             public void onDismissed(Snackbar snackbar, int event) {
                 super.onDismissed(snackbar, event);
                 view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                 //appsRecyclerViewAdapter.notifyItemChanged(position);
             }
         });
         snackbar.show();
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
             appsRecyclerViewAdapter.setDataList(list);
             swipeRefreshLayout.setRefreshing(false);
             if(progressDialog != null){
                 progressDialog.dismiss();
                 progressDialog = null;
             }
         }

         @Override
         public void onLoaderReset(Loader<List> listLoader) {

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
}
