package jone.helper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import jone.helper.R;

/**
 * Created by jone.sun on 2015/11/12.
 */
public class MainCalendarViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_label, tv_calendar;

    public MainCalendarViewHolder(Context context, View itemView) {
        super(itemView);
        tv_label = (TextView) itemView.findViewById(R.id.tv_label);
        tv_calendar = (TextView) itemView.findViewById(R.id.tv_calendar);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        itemView.setLayoutParams(new RecyclerView.LayoutParams((width / 7) - 2, RecyclerView.LayoutParams.MATCH_PARENT));
    }
}
