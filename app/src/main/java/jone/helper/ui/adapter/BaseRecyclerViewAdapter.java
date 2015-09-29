package jone.helper.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jone.sun on 2015/9/29.
 */
public abstract class BaseRecyclerViewAdapter<H extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<H> {
    private List<T> dataList = new ArrayList<>();

    @Override
    public void onBindViewHolder(final H holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public T getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    public void setDataList(List<T> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(T data){
        if(dataList == null){
            dataList = new ArrayList<>();
        }
        dataList.add(data);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}