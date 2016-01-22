package jone.helper.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.bean.PictureBean;
import jone.helper.bean.TnGouPicture;
import jone.helper.mvp.model.load.JSONObjectLoadDataModel;
import jone.helper.mvp.model.load.LoadDataModel;
import jone.helper.mvp.model.picture.TnGouPictureModel;
import jone.helper.mvp.presenter.loadData.LoadDataPresenter;
import jone.helper.mvp.presenter.loadData.LoadDataPresenterImpl;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;
import jone.helper.mvp.widget.loadData.LoadDataFragment;
import jone.helper.ui.adapter.TnGouPictureAdapter;
import jone.helper.ui.activities.base.BaseAppCompatWithLayoutActivity;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class PictureDetailActivity extends BaseAppCompatWithLayoutActivity {
    private static String title;
    @Override
    protected int getContentView() {
        return R.layout.activity_picture_detail;
    }

    @Override
    protected void findViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        int id = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("title");
        if(!TextUtils.isEmpty(title)){
            setTitle(title);
        }
        fragmentTransaction.add(R.id.fragment_container, PlaceholderFragment.newInstance(id));
        fragmentTransaction.commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends LoadDataFragment<TnGouPicture> {
        private static final String ID = "id";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int id) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ID, id);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
        @Override
        public LoadDataPresenter initLoadDataPresenter() {
            return new LoadDataPresenterImpl<TnGouPicture, JSONObject>(this) {
                @Override
                public LoadDataModel<TnGouPicture, JSONObject> getLoadDataModel() {
                    int id = getArguments().getInt("id", 0);
                    return new TnGouPictureModel(id);
                }
            };
        }

        @Override
        public LoadDataRecyclerViewAdapter<TnGouPicture> initAdapter(LoadMoreView loadMoreView) {
            return new TnGouPictureAdapter(getContext(), loadMoreView);
        }

        @Override
        public void onItemClick(View view, int position, TnGouPicture data) {
            List<TnGouPicture> tnGouPictureList = getAdapter().getDataList();
            ArrayList<PictureBean> pictureBeanArrayList = new ArrayList<>();
            for(TnGouPicture tnGouPicture : tnGouPictureList){
                pictureBeanArrayList.add(new PictureBean(title, "暂无说明", tnGouPicture.getSrc()));
            }
            PictureViewPagerActivity.open(getContext(), pictureBeanArrayList, position);
//            ZoomImageViewActivity.open(getActivity(),
//                    data.getSrc());
        }
    }
}
