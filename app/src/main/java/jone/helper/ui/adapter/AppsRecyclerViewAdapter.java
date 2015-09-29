package jone.helper.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.bean.News;

/**
 * Created by jone.sun on 2015/9/29.
 */
public class AppsRecyclerViewAdapter extends BaseRecyclerViewAdapter<AppsRecyclerViewHolder, ApplicationInfo> {

    private PackageManager packageManager;


    public AppsRecyclerViewAdapter(Context context) {
        packageManager = context.getPackageManager();
    }

    @Override
    public AppsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_list, parent, false);
        AppsRecyclerViewHolder mViewHolder = new AppsRecyclerViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(AppsRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ApplicationInfo applicationInfo = getItem(position);
        holder.app_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        holder.app_title.setText(applicationInfo.loadLabel(packageManager));
    }
}
