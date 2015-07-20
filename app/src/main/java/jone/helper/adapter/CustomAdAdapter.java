package jone.helper.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import jone.helper.AppConnect;
import jone.helper.R;
import jone.helper.model.customAd.entity.CustomAdInfo;

/**
 * Created by jone.sun on 2015/7/20.
 */
public class CustomAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<CustomAdInfo> dataList;
    public CustomAdAdapter(Context context , List<CustomAdInfo> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        return new VHItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_custom_ad, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i ){
        VHItem item = (VHItem) viewHolder;
        final CustomAdInfo customAdInfo = dataList.get(i);
        if(customAdInfo != null){
            item.txt_title.setText(customAdInfo.getAdName() != null ? customAdInfo.getAdName() + "(" + customAdInfo.getAdText() + ")" : "");
            item.txt_dec.setText(customAdInfo.getDescription() != null ? customAdInfo.getDescription() : "");
            if(customAdInfo.getAdIcon() != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    item.image_icon.setBackground(new BitmapDrawable(customAdInfo.getAdIcon()));
                }else {
                    item.image_icon.setBackgroundDrawable(new BitmapDrawable(customAdInfo.getAdIcon()));
                }
            }
            item.txt_dec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppConnect.getInstance(context).clickAd(context, customAdInfo.getAdId());
                }
            });
//            customAdInfo.setAdIcon(null);
//            Log.e("ss", GsonUtils.toJson(customAdInfo));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        public ImageView image_icon;
        public TextView txt_title, txt_dec;
        public NetworkImageView image_weather_pic;
        public VHItem(View v){
            super(v);
            image_icon = (ImageView) v.findViewById(R.id.image_icon);
            txt_title = (TextView) v.findViewById(R.id.txt_title);
            txt_dec = (TextView) v.findViewById(R.id.txt_dec);
            image_weather_pic = (NetworkImageView) v.findViewById(R.id.image_weather_pic);
        }
    }

    public void setDataList(List<CustomAdInfo> dataList) {
        this.dataList = dataList;
    }
}
