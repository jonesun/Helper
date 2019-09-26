package jone.helper.mvp.widget.loadData;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * GridLayout形式的加载数据Fragment
 * Created by jone.sun on 2016/1/14.
 */
public abstract class GridLayoutLoadDataFragment<T> extends LoadDataFragment<T> {
    private int spanCount;
    public GridLayoutLoadDataFragment(int spanCount) {
        this.spanCount = spanCount;
    }
    @Override
    public LinearLayoutManager initLayoutManager() {
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = getAdapter().getItemViewType(position);
                if(itemViewType == LoadDataRecyclerViewAdapter.TYPE_HEADER
                        || itemViewType == LoadDataRecyclerViewAdapter.TYPE_FOOTER){
                    return layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        return layoutManager;
    }
}
