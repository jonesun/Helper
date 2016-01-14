package jone.helper.mvp.view.loadData;

import android.content.Context;
import android.view.View;

/**
 *
 * Created by jone.sun on 2016/1/5.
 */
public interface LoadMoreView {
    /**
     * 初始化
     *
     * @param context
     */
    public View createView(Context context);

    /**
     * 显示普通保布局
     */
    public void showNormal();

    /**
     * 显示已经加载完成，没有更多数据的布局
     */
    public void showNomore();

    /**
     * 显示正在加载中的布局
     */
    public void showLoading();

    /**
     * 显示加载失败的布局
     */
    public void showFail();
}
