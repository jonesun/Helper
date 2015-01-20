package jone.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import jone.helper.R;
import jone.helper.bean.ToolBean;

public class ToolsAdapter extends ArrayAdapter<ToolBean> {
    private LayoutInflater inflater;
    private int resource;
    public ToolsAdapter(Context context) {
        super(context, R.layout.item_control_gridview);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = R.layout.item_control_gridview;
    }

    public void setData(List<ToolBean> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.imBtnControl = (ImageButton) convertView.findViewById(R.id.imBtnControl);
            holder.txtControl = (TextView) convertView.findViewById(R.id.txtControl);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ToolBean toolBean = getItem(position);
        if(toolBean.getImageSrcResource() != null){
            holder.imBtnControl.setImageResource(toolBean.getImageSrcResource());
        }
        if(toolBean.getBackgroundResource() != null){
            holder.imBtnControl.setBackgroundResource(toolBean.getBackgroundResource());
        }
        holder.imBtnControl.setOnClickListener(toolBean.getOnClickListener());
        holder.txtControl.setText(toolBean.getName());
        return convertView;
    }
    static class ViewHolder{
        ImageButton imBtnControl;
        TextView txtControl;
    }
}
