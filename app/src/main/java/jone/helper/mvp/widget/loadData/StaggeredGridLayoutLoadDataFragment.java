package jone.helper.mvp.widget.loadData;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by jone.sun on 2016/3/18.
 */
public abstract class StaggeredGridLayoutLoadDataFragment <T> extends LoadDataFragment<T> {
    private int spanCount;
    public StaggeredGridLayoutLoadDataFragment(int spanCount) {
        this.spanCount = spanCount;
    }
    @Override
    public RecyclerView.LayoutManager initLayoutManager() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                int itemViewType = getAdapter().getItemViewType(position);
//                if(itemViewType == LoadDataRecyclerViewAdapter.TYPE_HEADER
//                        || itemViewType == LoadDataRecyclerViewAdapter.TYPE_FOOTER){
//                    return layoutManager.getSpanCount();
//                }
//                return 1;
//            }
//        });
        return layoutManager;
    }
}
