package jone.helper.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Calendar;
import java.util.List;

import jone.helper.R;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.lib.volley.VolleyCommon;
import jone.helper.mvp.model.weather.entity.WeatherIndex;
import jone.helper.util.WeatherUtil;

/**
 * @author jone.sun on 2015/3/6.
 */
public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Weather weather;
    private List<WeatherData> weatherDataList;
    private Context mContext;
    private ImageLoader imageLoader;
    private Calendar calendar = Calendar.getInstance();
    public WeatherAdapter(Context context , List<WeatherData> weatherDataList){
        this.mContext = context;
        this.weatherDataList = weatherDataList;
        imageLoader = VolleyCommon.getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        if (i == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather, viewGroup, false);
            return new VHItem(v);
        } else if (i == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather_head, viewGroup, false);
            return new VHHeader(v);
        }
        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i ){
        if (viewHolder instanceof VHItem) {
            VHItem item = (VHItem) viewHolder;
            WeatherData weatherData = getItem(i);
            if(weatherData != null){
                StringBuffer weatherStringBuffer = new StringBuffer();
                weatherStringBuffer.append(weatherData.getDate()).append("\r\n")
                        .append("温度: ").append(weatherData.getTemperature()).append("\r\n")
                        .append("天气: ").append(weatherData.getWeather()).append("\r\n")
                        .append("风度: ").append(weatherData.getWind());
                item.txt_weather.setText(weatherStringBuffer);
                item.image_weather_pic.setDefaultImageResId(R.mipmap.ic_weather_default);
                item.image_weather_pic.setErrorImageResId(R.mipmap.ic_weather_default);
                if(calendar.get(Calendar.HOUR_OF_DAY) < 18){
                    item.image_weather_pic.setImageUrl(weatherData.getDayPictureUrl(),
                            imageLoader);
                }else {
                    item.image_weather_pic.setImageUrl(weatherData.getNightPictureUrl(),
                            imageLoader);
                }
                Bitmap bitmap = ((BitmapDrawable) item.image_weather_pic.getDrawable()).getBitmap();
                Palette p = Palette.from(bitmap).generate();
                item.txt_weather.setTextColor(p.getDarkVibrantColor(mContext.getResources().getColor(android.R.color.black)));
                item.root_view.setBackgroundColor(p.getLightVibrantColor(mContext.getResources().getColor(android.R.color.white)));

            }
        } else if (viewHolder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            VHHeader item = (VHHeader) viewHolder;
            if(weather != null){
                List<WeatherIndex> weatherIndexList = weather.getIndex();
                String pm25String = WeatherUtil.getPm25String(weather.getPm25());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("pm2.5: ").append(weather.getPm25()).append("(").append(pm25String).append(")");
                if(weatherIndexList != null && weatherIndexList.size() > 0){
                    for(WeatherIndex weatherIndex : weatherIndexList){
                        stringBuilder.append("\r\n").append(weatherIndex.getTitle())
                                .append("(").append(weatherIndex.getZs()).append(")").append(": ")
                                .append(weatherIndex.getDes());
                    }
                }
                item.txt_pm25.setText(stringBuilder.toString());
            }

        }

    }

    @Override
    public int getItemCount() {
        return weatherDataList == null ? 1 : weatherDataList.size() + 1;
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

    private WeatherData getItem(int position) {
        return weatherDataList == null ? null : weatherDataList.get(position - 1);
    }

    class VHHeader extends RecyclerView.ViewHolder {
        public TextView txt_pm25;
        public VHHeader(View itemView) {
            super(itemView);
            txt_pm25 = (TextView) itemView.findViewById(R.id.txt_pm25);
        }
    }

    class VHItem extends RecyclerView.ViewHolder {
        public ViewGroup root_view;
        public TextView txt_weather;
        public NetworkImageView image_weather_pic;
        public VHItem(View v){
            super(v);
            root_view = (ViewGroup) v.findViewById(R.id.root_view);
            txt_weather = (TextView) v.findViewById(R.id.txt_weather);
            image_weather_pic = (NetworkImageView) v.findViewById(R.id.image_weather_pic);
        }
    }

    public void setWeatherDataList(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
