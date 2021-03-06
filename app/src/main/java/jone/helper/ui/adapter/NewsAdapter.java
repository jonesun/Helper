package jone.helper.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.R;
import jone.helper.bean.News;
import jone.helper.model.AMapLocationTool;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.activities.EggsActivity;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;
import jone.helper.util.WeatherUtil;

/**
 * @author jone.sun on 2015/3/4.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<News> newsList;
    private EggsActivity eggsActivity;
    private StringBuffer weatherStringBuffer = null;
    private Calendar calendar = Calendar.getInstance();
    private FestivalUtil festivalUtil;
    private StringBuffer festival = null;
    public NewsAdapter(EggsActivity eggsActivity, List<News> newsList){
        this.eggsActivity = eggsActivity;
        this.newsList = newsList;
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
                item.from.setText(news.getFrom());
                item.mTextView.setTag(news);
                item.from.setTag(news);
                item.mTextView.setOnClickListener(this);
                item.from.setOnClickListener(this);
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
//            if(menuActivity != null){
//                menuActivity.changeFragment(WeatherFragment.getInstance());
//            }
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
        public TextView mTextView, from;
        public VHItem(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.name);
            from = (TextView) v.findViewById(R.id.from);
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
                    festival.append(str);
//                    festival += str + " ";
                    System.out.println(str);
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
            if(SystemUtil.isNetworkAlive(eggsActivity)){
                txt_weather.setText("loading...");
                txt_weather.setSelected(true);
                AMapLocationTool.getInstance().startLocation(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation != null) {
                            String city = aMapLocation.getCity();
                            if (!TextUtils.isEmpty(city)) {
                                UmengUtil.get_location(eggsActivity, "baiduLocation", city);
                                WeatherUtil.getWeatherInfoByCity(city.replace("市", ""), new WeatherUtil.WeatherInfoListener() {
                                    @Override
                                    public void onResponse(Weather weatherInfo) {
                                        if (weatherInfo != null) {
                                            weatherStringBuffer = new StringBuffer();
                                            weatherStringBuffer.append("当前城市: " + weatherInfo.getCurrentCity()).append("\t");
                                            List<WeatherData> weatherDatas = weatherInfo.getWeather_data();
                                            if (weatherDatas != null && weatherDatas.size() > 0) {
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
                            }
                        }
                        AMapLocationTool.getInstance().stopLocation();
                    }
                });
            }else {
                txt_weather.setText("天气获取失败");
            }
        }else {
            txt_weather.setText(weatherStringBuffer);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onClick(View view , News news);
    }
}
