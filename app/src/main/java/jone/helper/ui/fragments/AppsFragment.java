 package jone.helper.ui.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jone.helper.R;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.adapter.AppsRecyclerViewAdapter;
import jone.helper.ui.adapter.AppsRecyclerViewHolder;
import jone.helper.ui.adapter.BaseRecyclerViewAdapter;
import jone.helper.ui.fragments.base.BaseRecyclerViewFragment;

 public class AppsFragment extends BaseRecyclerViewFragment<JoneAppManagerActivity, AppsRecyclerViewHolder, ApplicationInfo> implements AppsRecyclerViewAdapter.OnItemClickListener{
     private final String TAG = AppsFragment.class.getSimpleName();
     private AppsRecyclerViewAdapter appsRecyclerViewAdapter;

     private PackageManager packageManager;
     public static AppsFragment newInstance() {
        return new AppsFragment();
     }

     public AppsFragment() {
        // Required empty public constructor
     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

     @Override
     public void initConfig() {
         super.initConfig();
         setShowMode(SHOW_MODE_HAVE_REFRESH);
         enableDebugLogging(true);
         setDebugTag(TAG);
         packageManager = getActivity().getPackageManager();
     }

     @Override
     public LinearLayoutManager getLayoutManager() {
         return new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
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
                 .setAction(getString(R.string.uninstall), new View.OnClickListener() {
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

     @Override
     public BaseRecyclerViewAdapter<AppsRecyclerViewHolder, ApplicationInfo> getAdapter() {
         appsRecyclerViewAdapter = new AppsRecyclerViewAdapter(getActivity());
         appsRecyclerViewAdapter.setOnItemClickListener(this);
         return appsRecyclerViewAdapter;
     }

     @Override
     public List<ApplicationInfo> getList() {
//         try {
//             Thread.sleep(3000);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//         return new ArrayList<>();
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
