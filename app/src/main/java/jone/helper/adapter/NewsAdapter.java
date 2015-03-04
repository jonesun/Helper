package jone.helper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import jone.helper.App;
import jone.helper.BitmapCache;
import jone.helper.R;
import jone.helper.bean.News;

/**
 * @author jone.sun on 2015/3/4.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<News> newsList;
    private Context mContext;
    private ImageLoader imageLoader;
    public NewsAdapter(Context context , List<News> newsList){
        this.mContext = context;
        this.newsList = newsList;
        imageLoader = new ImageLoader(App.getInstance().getVolleyCommon().getmRequestQueue(),
                new BitmapCache());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        if (i == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_card_view, viewGroup, false);
            return new VHItem(v);
        } else if (i == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_head, viewGroup, false);
            return new VHHeader(v);
        }
        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i ){
        if (viewHolder instanceof VHItem) {
            VHItem item = (VHItem) viewHolder;
            News news = getItem(i);
            if(news != null){
                item.mTextView.setText(news.getTitle());
                item.mImageView.setDefaultImageResId(R.drawable.default_news);
                item.mImageView.setErrorImageResId(R.drawable.default_news);
                item.mImageView.setImageUrl(news.getImageUrl(),
                        imageLoader);
            }
        } else if (viewHolder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
        }
    }


    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 1 : newsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private News getItem(int position) {
        return newsList == null ? null : newsList.get(position - 1);
    }

    class VHItem extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public NetworkImageView mImageView;
        public VHItem(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.name);
            mImageView = (NetworkImageView) v.findViewById(R.id.pic);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {

        public VHHeader(View itemView) {
            super(itemView);
        }
    }
}
