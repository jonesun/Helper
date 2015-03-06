package jone.helper.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.waps.AppConnect;
import jone.helper.App;
import jone.helper.BitmapCache;
import jone.helper.R;
import jone.helper.bean.News;
import jone.helper.bean.Weather;
import jone.helper.bean.WeatherData;
import jone.helper.lib.util.BitmapUtil;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.main.MenuActivity;
import jone.helper.ui.main.WeatherFragment;
import jone.helper.util.FestivalUtil;
import jone.helper.util.WeatherUtil;

/**
 * @author jone.sun on 2015/3/4.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<News> newsList;
    private MenuActivity menuActivity;
    private ImageLoader imageLoader;
    private StringBuffer weatherStringBuffer = null;
    private Calendar calendar = Calendar.getInstance();
    private FestivalUtil festivalUtil;
    private StringBuffer festival = null;
    public NewsAdapter(MenuActivity menuActivity, List<News> newsList){
        this.menuActivity = menuActivity;
        this.newsList = newsList;
        imageLoader = new ImageLoader(App.getInstance().getVolleyCommon().getmRequestQueue(),
                new BitmapCache());
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        if (i == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_card_view, viewGroup, false);
            v.setOnClickListener(this);
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
            viewHolder.itemView.setTag(news);
        } else if (viewHolder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            VHHeader item = (VHHeader) viewHolder;
            showDate(item.txt_date, item.txt_festival);
            showWeatherInfo(item.txt_weather);
            item.txt_date.setOnClickListener(openWeatherFragment);
            item.txt_festival.setOnClickListener(openWeatherFragment);
        }
    }

    private View.OnClickListener openWeatherFragment = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(menuActivity != null){
                menuActivity.changeFragment(WeatherFragment.getInstance());
            }
        }
    };

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

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null &&
                view.getTag()!= null
                && view.getTag() instanceof News) {
            mOnItemClickListener.onClick(view,(News)view.getTag());
        }
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
        public TextView txt_date;
        public TextView txt_festival;
        public TextView txt_weather;
        public VHHeader(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_festival = (TextView) itemView.findViewById(R.id.txt_festival);
            txt_weather = (TextView) itemView.findViewById(R.id.txt_weather);
        }
    }

    private void showDate(TextView txt_date, TextView txt_festival){
        if(festivalUtil == null){
            festivalUtil = new FestivalUtil(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
        }
        txt_date.setText(calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + "周" + FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1)  +" 农历:" + festivalUtil.getChineseDate());
        ArrayList<String> fest = festivalUtil.getFestVals();
        if(festival == null){
            festival = new StringBuffer();
//            String festival = "";
            if(fest.size() > 0){
                for(String str:fest){
                    festival.append(str + "(" + FestivalUtil.getPinYin(str).trim() + ")" +" ");
//                    festival += str + " ";
                    System.out.println(str + "(" + FestivalUtil.getPinYin(str, "_").trim() + ")");
                }
                txt_festival.setText("今天是: " + festival);
            }else {
                txt_festival.setText("今天没有节日");
            }
        }else {
            txt_festival.setText("今天是: " + festival);
        }

    }

    private void showWeatherInfo(final TextView txt_weather){
        if(weatherStringBuffer == null){
            if(SystemUtil.isNetworkAlive(menuActivity)){
                txt_weather.setText("loading...");
                txt_weather.setSelected(true);
                WeatherUtil.getLocationCityWeatherInfo(new WeatherUtil.WeatherInfoListener() {
                    @Override
                    public void onResponse(Weather weatherInfo) {
                        if (weatherInfo != null) {
                            weatherStringBuffer = new StringBuffer();
                            weatherStringBuffer.append("当前城市: " + weatherInfo.getCurrentCity()).append("\t");
                            List<WeatherData> weatherDatas = weatherInfo.getWeather_data();
                            if(weatherDatas != null && weatherDatas.size() > 0){
                                WeatherData weatherData = weatherDatas.get(0);
                                weatherStringBuffer.append("温度: " + weatherData.getTemperature() + "\t"
                                        + "天气: " + weatherData.getWeather() + "(" + weatherData.getWind() + ")\t"
                                        + "发布时间: " + weatherData.getDate());
                            }
                            txt_weather.setText(weatherStringBuffer.toString());
                        } else {
                            txt_weather.setText("天气获取失败");
                        }
                    }
                });
            }else {
                txt_weather.setText("天气获取失败");
            }
        }else {
            txt_weather.setText(weatherStringBuffer);
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onClick(View view , News news);
    }
}
