package jone.helper.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.TnGouGallery;
import jone.helper.lib.model.imageLoader.ImageLoaderListener;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;

/**
 * Created by jone.sun on 2016/1/14.
 */
public class TnGouGalleryAdapter extends LoadDataRecyclerViewAdapter<TnGouGallery> {
    public TnGouGalleryAdapter(Context context, LoadMoreView loadMoreView) {
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
            TnGouGallery data = getItem(position);
            PictureItemViewHolder viewHolder = (PictureItemViewHolder) holder;
            viewHolder.mTitle.setText(data.getTitle());
            viewHolder.mImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            viewHolder.mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            App.getImageLoader().display(getContext(),
                    viewHolder.mImage, data.getImg(),
                    R.mipmap.ic_image_loading, R.mipmap.ic_image_loadfail);
//            App.getImageLoader().getBitmap(getContext(), data.getImg(), new ImageLoaderListener() {
//                @Override
//                public void onDone(Bitmap bitmap) {
//                    int maxHeight = dp2px(getContext(), 800);
//                    int height = bitmap.getHeight();
//                    if (height > maxHeight) height = maxHeight;
//                    viewHolder.mImage.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, height));
//                }
//            });

        }
    }

    public static int dp2px(Context context, int dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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
