package jone.helper.ui.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.bean.NotebookData;
import jone.helper.ui.activities.EditNotebookActivity;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookAdapter extends ArrayAdapter<NotebookData> {
    private boolean[] selectIds; //存储选择的记录的id
    private LayoutInflater inflater;
    private PackageManager packageManager;
    public NotebookAdapter(Context context) {
        super(context, R.layout.item_notebook_list);
        packageManager = context.getPackageManager();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_notebook_list, null);
            holder = new ViewHolder();
            holder.note_content = (TextView) convertView.findViewById(R.id.note_content);
            holder.note_date = (TextView) convertView.findViewById(R.id.note_date);
            holder.note_img_thumbtack = (ImageView) convertView.findViewById(R.id.note_img_thumbtack);
            holder.note_title = (RelativeLayout) convertView.findViewById(R.id.note_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NotebookData notebookData = getItem(position);
        holder.note_date.setText(notebookData.getDate());
        holder.note_content.setText(notebookData.getContent());
        holder.note_img_thumbtack.setImageResource(EditNotebookActivity.sThumbtackImgs[notebookData.getColor()]);
        holder.note_content.setBackgroundColor(EditNotebookActivity.sBackGrounds[notebookData.getColor()]);
        holder.note_title.setBackgroundColor(EditNotebookActivity.sTitleBackGrounds[notebookData.getColor()]);
        final int pos = position;
        if(selectIds!= null && selectIds.length > 0){
            if(selectIds[pos]){
                holder.note_content.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_red_light));
            }else {
                holder.note_content.setBackgroundColor(EditNotebookActivity.sBackGrounds[notebookData.getColor()]);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView note_content, note_date;
        RelativeLayout note_title;
        ImageView note_img_thumbtack;
    }

    public void setData(List dataList){
        this.clear();
        this.addAll(dataList);
        selectIds = (new boolean[dataList.size()]);
        this.notifyDataSetChanged();
    }

    public void checkSelect(ActionMode actionMode, int position, boolean isSelect) {
        selectIds[position] = isSelect;
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

    public List<NotebookData> getSelectDeleteData() {
        List<NotebookData> list = new ArrayList<>();
        for(int i = 0; i < selectIds.length; i++){
            if(selectIds[i]){
                list.add(getItem(i));
            }
        }
        return list;
    }
}
