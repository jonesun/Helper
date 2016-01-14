package jone.helper.ui.fragments;


import android.view.View;

import jone.helper.bean.LaiFuDaoPicture;
import jone.helper.mvp.model.load.LoadDataModel;
import jone.helper.mvp.model.load.StringLoadDataModel;
import jone.helper.mvp.model.picture.LaiFuDaoPictureNewModel;
import jone.helper.mvp.presenter.loadData.LoadDataPresenter;
import jone.helper.mvp.presenter.loadData.LoadDataPresenterImpl;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;
import jone.helper.mvp.widget.loadData.LoadDataFragment;
import jone.helper.ui.adapter.LaiFuDaoPictureRecyclerViewAdapter;
import jone.helper.ui.activities.ZoomImageViewActivity;

/**
 * Created by jone.sun on 2016/1/12.
 */
public class LaiFuDaoPictureFragment extends LoadDataFragment<LaiFuDaoPicture> {

    @Override
    public LoadDataPresenter initLoadDataPresenter() {
        return new LoadDataPresenterImpl<LaiFuDaoPicture, String>(this) {
            @Override
            public LoadDataModel<LaiFuDaoPicture, String> getLoadDataModel() {
                return new LaiFuDaoPictureNewModel();
            }
        };
    }

    @Override
    public LoadDataRecyclerViewAdapter<LaiFuDaoPicture> initAdapter(LoadMoreView loadMoreView) {
        return new LaiFuDaoPictureRecyclerViewAdapter(getActivity(), loadMoreView);
    }

    @Override
    public void onItemClick(View view, int position, LaiFuDaoPicture data) {
        ZoomImageViewActivity.open(getActivity(), data.getSourceurl());
    }
}