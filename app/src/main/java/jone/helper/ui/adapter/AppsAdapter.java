package jone.helper.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;

/**
 * @author jone.sun on 2015/3/24.
 */
public class AppsAdapter extends ArrayAdapter<ApplicationInfo> {
    private boolean[] selectIds; //存储选择的记录的id
    private LayoutInflater inflater;
    private PackageManager packageManager;
    public AppsAdapter(Context context) {
        super(context, R.layout.item_app_list);
        packageManager = context.getPackageManager();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_app_list, null);
            holder = new ViewHolder();
            holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.app_title = (TextView) convertView.findViewById(R.id.app_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ApplicationInfo applicationInfo = getItem(position);
        holder.app_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        holder.app_title.setText(applicationInfo.loadLabel(packageManager));
        final int pos = position;
        if(selectIds!= null && selectIds.length > 0){
            if(selectIds[pos]){
                holder.app_title.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
            }else {
                holder.app_title.setTextColor(getContext().getResources().getColor(android.R.color.white));
            }
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView app_icon;
        TextView app_title;
    }

    public void setData(List dataList){
        this.clear();
        this.addAll(dataList);
        selectIds = (new boolean[dataList.size()]);
        this.notifyDataSetChanged();
    }

    public void checkSelect(ActionMode actionMode, int position) {
        for(int i = 0; i < selectIds.length; i++){
            if(i == position){
                selectIds[i] = true;
            }else {
                selectIds[i] = false;
            }
        }
        notifyDataSetChanged();
        actionMode.setTitle("选择了" + getSelectCount() + "项");
    }

    public void selectAll(ActionMode actionMode) {
        for(int i = 0; i < selectIds.length; i++){
            selectIds[i] = true;
        }
        notifyDataSetChanged();
        actionMode.setTitle("选择了" + getSelectCount() + "项");
    }

    public void unSelectAll(ActionMode actionMode) {
        for(int i = 0; i < selectIds.length; i++){
            selectIds[i] = false;
        }
        notifyDataSetChanged();
        actionMode.setTitle("");
    }

    public int getSelectCount() {
        int count = 0;
        for(int i = 0; i < selectIds.length; i++){
            if(selectIds[i]){
                count ++;
            }
        }
        return count;
    }

    public List<ApplicationInfo> getSelectDeleteData() {
        List<ApplicationInfo> list = new ArrayList<>();
        for(int i = 0; i < selectIds.length; i++){
            if(selectIds[i]){
                list.add(getItem(i));
            }
        }
        return list;
    }
}
