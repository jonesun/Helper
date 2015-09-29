package jone.helper.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jone.helper.R;

/**
 * Created by Monkey on 2015/6/29.
 */
public class AppsRecyclerViewHolder extends RecyclerView.ViewHolder {

    public ImageView app_icon;
    public TextView app_title;

    public AppsRecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setPadding(2, 2, 2, 2);
        app_icon = (ImageView) itemView.findViewById(R.id.app_icon);
        app_title = (TextView) itemView.findViewById(R.id.app_title);
    }
}
