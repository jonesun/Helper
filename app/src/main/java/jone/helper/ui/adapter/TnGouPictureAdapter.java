package jone.helper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.TnGouPicture;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class TnGouPictureAdapter extends LoadDataRecyclerViewAdapter<TnGouPicture> {
    public TnGouPictureAdapter(Context context, LoadMoreView loadMoreView) {
        super(context, loadMoreView);
    }

    @Override
    public ItemViewHolder getItemViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picture, parent, false);
        return new PictureItemViewHolder(v);
    }

    @Override
    public void bindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PictureItemViewHolder) {
            TnGouPicture data = getItem(position);
            PictureItemViewHolder viewHolder = (PictureItemViewHolder) holder;
            viewHolder.mTitle.setVisibility(View.GONE);
            viewHolder.mImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 700));
            App.getImageLoader().display(getContext(),
                    viewHolder.mImage, data.getSrc(),
                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);
        }
    }

    public class PictureItemViewHolder extends ItemViewHolder{

        public TextView mTitle;
        public ImageView mImage;

        public PictureItemViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.tvTitle);
            mImage = (ImageView) v.findViewById(R.id.ivImage);
        }
    }
}
