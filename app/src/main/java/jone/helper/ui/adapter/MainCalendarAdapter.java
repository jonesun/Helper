package jone.helper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import jone.helper.R;
import jone.helper.bean.HomeMainCalendarBean;
import jone.helper.model.calendar.CalendarActivity;

/**
 * Created by jone.sun on 2015/11/12.
 */
public class MainCalendarAdapter extends BaseRecyclerViewAdapter<MainCalendarViewHolder, HomeMainCalendarBean>{

    public MainCalendarAdapter(Context context) {
        super(context);
    }

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
        holder.tv_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), CalendarActivity.class));
            }
        });
        if(homeMainCalendarBean.isToday()){
            holder.tv_calendar.setBackgroundResource(R.drawable.today_bg);
        }
    }
}