package jone.helper.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import org.json.JSONObject;

import jone.helper.bean.TnGouGallery;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;
import jone.helper.mvp.model.load.LoadDataModel;
import jone.helper.mvp.model.picture.TnGouGalleryModel;
import jone.helper.mvp.presenter.loadData.LoadDataPresenter;
import jone.helper.mvp.presenter.loadData.LoadDataPresenterImpl;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;
import jone.helper.mvp.widget.loadData.LoadDataFragment;
import jone.helper.ui.adapter.TnGouGalleryAdapter;
import jone.helper.ui.activities.PictureDetailActivity;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class TnGouGalleryFragment extends LoadDataFragment<TnGouGallery> {
    public static TnGouGalleryFragment newInstance(int classId, String classTitle){
        TnGouGalleryFragment fragment = new TnGouGalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("classId", classId);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public LoadDataPresenter initLoadDataPresenter() {
        return new LoadDataPresenterImpl<TnGouGallery, JSONObject>(this) {
            @Override
            public LoadDataModel<TnGouGallery, JSONObject> getLoadDataModel() {
                int classId = getArguments().getInt("classId", 0);
                return new TnGouGalleryModel(classId);
            }
        };
    }

    @Override
    public LoadDataRecyclerViewAdapter<TnGouGallery> initAdapter(LoadMoreView loadMoreView) {
        return new TnGouGalleryAdapter(getContext(), loadMoreView);
    }

    @Override
    public void onItemClick(View view, int position, TnGouGallery data) {
        Intent intent = new Intent(getActivity(), PictureDetailActivity.class);
        intent.putExtra("id", data.getId());
        intent.putExtra("title", data.getTitle());
        startActivity(intent);
    }
}
