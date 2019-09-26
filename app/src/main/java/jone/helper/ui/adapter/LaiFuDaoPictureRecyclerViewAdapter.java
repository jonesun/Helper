package jone.helper.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.LaiFuDaoPicture;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.ToolsUtil;
import jone.helper.mvp.widget.loadData.LoadDataRecyclerViewAdapter;
import jone.helper.mvp.view.loadData.LoadMoreView;

/**
 * Created by jone.sun on 2016/1/12.
 */
public class LaiFuDaoPictureRecyclerViewAdapter extends LoadDataRecyclerViewAdapter<LaiFuDaoPicture>{
    private int mMaxWidth;
    private int mMaxHeight;
    public LaiFuDaoPictureRecyclerViewAdapter(Context context, LoadMoreView loadMoreView) {
        super(context, loadMoreView);
        mMaxWidth = ToolsUtil.getWidthInPx(context) - 20;
        mMaxHeight = ToolsUtil.getHeightInPx(context) - ToolsUtil.getStatusHeight(context) -
                ToolsUtil.dip2px(context, 96);
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
            LaiFuDaoPicture data = GsonUtils.loadAs(GsonUtils.toJson(getItem(position)), LaiFuDaoPicture.class);
            PictureItemViewHolder viewHolder = (PictureItemViewHolder) holder;
            viewHolder.mTitle.setText(data.getTitle());
            try {
                int width = Integer.parseInt(data.getWidth());
                int height = Integer.parseInt(data.getHeight());
                float scale = (float)width / (float) mMaxWidth;
                height = (int)(height / scale);
                if(height > mMaxHeight) {
                    height = mMaxHeight;
                }
                viewHolder.mImage.setLayoutParams(new LinearLayout.LayoutParams(mMaxWidth, height));
            }catch (Exception e){
                Log.e("PictureRecycler", "", e);
            }
            App.getImageLoader().display(getContext(),
                    viewHolder.mImage, data.getThumburl(),
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
