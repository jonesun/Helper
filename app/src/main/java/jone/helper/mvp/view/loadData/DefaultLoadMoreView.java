package jone.helper.mvp.view.loadData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import jone.helper.R;

/**
 * Created by jone.sun on 2016/1/5.
 */
public class DefaultLoadMoreView implements LoadMoreView {
    private ProgressBar progress_bar;
    private TextView text_info;
    protected View.OnClickListener onClickRefreshListener;
    public DefaultLoadMoreView(View.OnClickListener onClickRefreshListener){
        this.onClickRefreshListener = onClickRefreshListener;
    }
    public DefaultLoadMoreView(){

    }
    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_load_data_recycler_footer, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
        text_info = (TextView) view.findViewById(R.id.text_info);
        showNormal();
        return view;
    }

    @Override
    public void showNormal() {
        if(progress_bar != null && text_info != null){
            progress_bar.setVisibility(View.GONE);
            text_info.setText("");
            text_info.setOnClickListener(onClickRefreshListener);
        }
    }

    @Override
    public void showNomore() {
        if(progress_bar != null && text_info != null){
            progress_bar.setVisibility(View.GONE);
            text_info.setText("已经加载完毕");
            text_info.setOnClickListener(null);
            text_info.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoading() {
        if(progress_bar != null && text_info != null){
            progress_bar.setVisibility(View.VISIBLE);
            text_info.setVisibility(View.VISIBLE);
            text_info.setText("正在加载中...");
            text_info.setOnClickListener(null);
        }
    }

    @Override
    public void showFail() {
        if(progress_bar != null && text_info != null){
            progress_bar.setVisibility(View.GONE);
            text_info.setText("加载失败");
            text_info.setOnClickListener(onClickRefreshListener);
        }
    }
}
