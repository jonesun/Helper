package jone.helper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.App;
import jone.helper.BitmapCache;
import jone.helper.R;
import jone.helper.bean.WeatherData;
import jone.helper.lib.volley.VolleyCommon;

/**
 * @author jone.sun on 2015/3/6.
 */
public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<WeatherData> weatherDataList;
    private Context mContext;
    private ImageLoader imageLoader;
    private Calendar calendar = Calendar.getInstance();
    public WeatherAdapter(Context context , List<WeatherData> weatherDataList){
        this.mContext = context;
        this.weatherDataList = weatherDataList;
        imageLoader = new ImageLoader(VolleyCommon.getInstance(App.getInstance()).getmRequestQueue(),
                new BitmapCache());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        return new VHItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i ){
        VHItem item = (VHItem) viewHolder;
        WeatherData weatherData = weatherDataList.get(i);
        if(weatherData != null){
            StringBuffer weatherStringBuffer = new StringBuffer();
            weatherStringBuffer.append(weatherData.getDate()).append("\r\n")
                    .append("温度: ").append(weatherData.getTemperature()).append("\r\n")
                    .append("天气: ").append(weatherData.getWeather()).append("\r\n")
                    .append("风度: ").append( weatherData.getWind());
            item.txt_weather.setText(weatherStringBuffer);
            item.image_weather_pic.setDefaultImageResId(R.drawable.ic_weather_default);
            item.image_weather_pic.setErrorImageResId(R.drawable.ic_weather_default);
            if(calendar.get(Calendar.HOUR_OF_DAY) < 18){
                item.image_weather_pic.setImageUrl(weatherData.getDayPictureUrl(),
                        imageLoader);
            }else {
                item.image_weather_pic.setImageUrl(weatherData.getNightPictureUrl(),
                        imageLoader);
            }
        }
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        public TextView txt_weather;
        public NetworkImageView image_weather_pic;
        public VHItem(View v){
            super(v);
            txt_weather = (TextView) v.findViewById(R.id.txt_weather);
            image_weather_pic = (NetworkImageView) v.findViewById(R.id.image_weather_pic);
        }
    }

    public void setWeatherDataList(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }
}
