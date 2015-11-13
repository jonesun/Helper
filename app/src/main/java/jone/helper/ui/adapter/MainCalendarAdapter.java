package jone.helper.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import jone.helper.R;
import jone.helper.bean.HomeMainCalendarBean;

/**
 * Created by jone.sun on 2015/11/12.
 */
public class MainCalendarAdapter extends BaseRecyclerViewAdapter<MainCalendarViewHolder, HomeMainCalendarBean>{

    @Override
    public MainCalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainCalendarViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_calendar, parent, false));
    }

    @Override
    public void onBindViewHolder(MainCalendarViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        HomeMainCalendarBean homeMainCalendarBean = getItem(position);
        holder.tv_label.setText(homeMainCalendarBean.getLabel());
        holder.tv_calendar.setText(homeMainCalendarBean.getCalendar());
        if(homeMainCalendarBean.isToday()){
            holder.tv_calendar.setBackgroundResource(R.drawable.today_bg);
        }
    }
}