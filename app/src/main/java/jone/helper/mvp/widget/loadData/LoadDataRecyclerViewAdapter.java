package jone.helper.mvp.widget.loadData;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jone.helper.mvp.view.loadData.DefaultLoadMoreView;
import jone.helper.mvp.view.loadData.LoadMoreView;

/**
 * LoadDataMVP对应View-Adapter
 * Created by jone.sun on 2016/1/5.
 */
public abstract class LoadDataRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    private List<T> dataList = new ArrayList<>();
    private boolean mShowFooter = true;
    private Context context;

    private View headerView;
    private LoadMoreView loadMoreView;

    private int headerCount = 0;
    private int footerCount = 0;

    private OnItemClickListener<T> mOnItemClickListener;

    public LoadDataRecyclerViewAdapter(Context context, LoadMoreView loadMoreView) {
        this.context = context;
        this.loadMoreView = loadMoreView;
        initHeadAndFooterCount();
    }

    public LoadDataRecyclerViewAdapter(Context context, View headerView, LoadMoreView loadMoreView) {
        this.context = context;
        this.headerView = headerView;
        this.loadMoreView = loadMoreView;
        initHeadAndFooterCount();
    }

    private void initHeadAndFooterCount(){
        headerCount = headerView == null?0:1;
        footerCount = loadMoreView == null?0:1;
    }

    public void setDataList(List<T> dataList) {
        if(dataList == null){
            dataList = new ArrayList<>();
        }
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    public void addDataList(List<T> dataList) {
        if(dataList != null){
            this.dataList.addAll(dataList);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(isHeader(position)){
            return TYPE_HEADER;
        }
        if(isFooter(position)){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public T getItem(int position) {
        if(getItemViewType(position) != TYPE_ITEM
                || dataList == null
                || position > dataList.size()){
            return null;
        }
        return dataList.get(position - headerCount);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new FooterOfHeaderViewHolder(headerView);
        }
        if (viewType == TYPE_FOOTER) {
            if(loadMoreView == null){
                loadMoreView = new DefaultLoadMoreView();
            }
            return new FooterOfHeaderViewHolder(loadMoreView.createView(parent.getContext()));
        }
        return getItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            return;
        }
        if (isFooter(position)) {
            return;
        }
        bindItemViewHolder(holder, position);// Subtract 1 for header
    }

    public abstract ItemViewHolder getItemViewHolder(ViewGroup parent);

    public abstract void bindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        int headerAndFooterCount = headerCount + footerCount;
        if(dataList == null) {
            return headerAndFooterCount;
        }
        return dataList.size() + headerAndFooterCount;
    }

    public boolean isHeader(int position) {
        if(headerCount==0){
            return false;
        }
        return position == 0;
    }
    public boolean isFooter(int position) {
        if(footerCount == 0){
            return false;
        }
        return position == dataList.size() + headerCount;// add 1 for footer
    }

    public void setShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
        if(mShowFooter){
            initHeadAndFooterCount();
        }else {
            footerCount = 0;
        }
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class FooterOfHeaderViewHolder extends RecyclerView.ViewHolder {

        public FooterOfHeaderViewHolder(View view) {
            super(view);
        }

    }

    public interface OnItemClickListener<T> {
        public void onItemClick(View view, int position, T data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getAdapterPosition(), getItem(getAdapterPosition()));
            }
        }
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Context getContext() {
        return context;
    }

    public LoadMoreView getLoadMoreView() {
        return loadMoreView;
    }
}
