package jone.helper.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.SystemClock;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jone.helper.BuildConfig;
import jone.helper.R;
import jone.helper.lib.util.Utils;
import jone.helper.model.AMapLocationTool;
import jone.helper.mvp.model.weather.entity.Weather;
import jone.helper.mvp.model.weather.entity.WeatherData;
import jone.helper.mvp.presenter.weather.WeatherPresenter;
import jone.helper.mvp.presenter.weather.impl.BaiduWeatherPresenter;
import jone.helper.mvp.view.weather.WeatherView;
import jone.helper.ui.activities.EggsActivity;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.activities.WeatherSelectCityActivity;
import jone.helper.ui.adapter.WeatherAdapter;
import jone.helper.ui.fragments.base.BaseFragment;
import jone.helper.util.FestivalUtil;
import jone.helper.util.UmengUtil;

/**
 * Created by jone.sun on 2015/7/2.
 */
public class WeatherFragment extends BaseFragment<HelperMainActivity> implements WeatherView {
    private static final String TAG = WeatherFragment.class.getSimpleName();
    private WeatherAdapter weatherAdapter;
    private Dialog loadingDialog;

    private LinearLayout layout_top;
    private TextView txt_date, txt_festival;
    private Button btn_city;
    private RecyclerView rv_weather;

    private WeatherPresenter weatherPresenter;

    private List<WeatherData> weatherDataList = new ArrayList<>();

    public static final int resultCode = 10001;
    private static WeatherFragment instance = null;
    public static WeatherFragment getInstance(){
        if(instance == null){
            instance = new WeatherFragment();
        }
        return instance;
    }
    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void findViews(View view) {
        layout_top = findView(view, R.id.layout_top);
        txt_date = findView(view, R.id.txt_date);
        txt_festival = findView(view, R.id.txt_festival);

        btn_city = findView(view, R.id.btn_city);
        rv_weather= findView(view, R.id.rv_weather);
        rv_weather.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        rv_weather.setItemAnimator(new DefaultItemAnimator());
        rv_weather.setHasFixedSize(true);

        weatherAdapter = new WeatherAdapter(getActivity(), weatherDataList);
        rv_weather.setAdapter(weatherAdapter);
        showDate();
        showEggs();
        btn_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), WeatherSelectCityActivity.class), resultCode);
            }
        });

        weatherPresenter = new BaiduWeatherPresenter(this); //传入WeatherView
        loadingDialog = new ProgressDialog(getHostActivity());
        loadingDialog.setTitle("加载天气中...");
        if(Utils.isNetworkAlive(getHostActivity())){
            AMapLocationTool.getInstance().startLocation(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    // map view 销毁后不在处理新接收的位置
                    if (aMapLocation != null) {
                        String city = aMapLocation.getCity();
                        Log.e("sss", "city: " + city);
                        getWeatherByCity(aMapLocation.getCity().replace("市", ""));
                    }
                    AMapLocationTool.getInstance().stopLocation();
                }
            });
        }else {
            btn_city.setText("网络连接失败");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AMapLocationTool.getInstance().destroy();
    }

    private void getWeatherByCity(String city){
        if(Utils.isNetworkAlive(getHostActivity())){
            if(weatherPresenter != null){
                weatherPresenter.getWeather(getHostActivity(), city);
            }
        }else {
            btn_city.setText(getString(R.string.no_network));
        }
    }

    long[] mHits = new long[3];
    private void showEggs(){
        if(layout_top != null){
            layout_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                    mHits[mHits.length-1] = SystemClock.uptimeMillis();
                    if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                        if(BuildConfig.FLAVOR.equals("baidumap")){
                            //startActivity(new Intent(getActivity(), BaiduMapActivity.class));
                        }else {
                            startActivity(new Intent(getHostActivity(), EggsActivity.class));
                        }
                    }
                }
            });
        }
    }

    private void showDate(){
        Calendar calendar = Calendar.getInstance();
        FestivalUtil festivalUtil = new FestivalUtil(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
        txt_date.setText(getString(R.string.now_date,
                calendar.get(Calendar.YEAR),
                (calendar.get(Calendar.MONTH) + 1),
                calendar.get(Calendar.DAY_OF_MONTH),
                FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1),
                festivalUtil.getChineseDate()));
//        txt_date.setText(calendar.get(Calendar.YEAR) + "年" +
//                (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + "周" + FestivalUtil.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK) - 1)  +" 农历:" + festivalUtil.getChineseDate());
        ArrayList<String> fest = festivalUtil.getFestVals();
        StringBuilder festival = new StringBuilder();
        if(fest.size() > 0){
            for(String str:fest){
                festival.append(str).append(" ");
            }
            txt_festival.setText(getString(R.string.today_festival, festival));
        }else {
            txt_festival.setText(getString(R.string.today_no_festival));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            String result = data.getExtras().getString("result");
            getWeatherByCity(result);
        }
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    @Override
    public void showError(String reason) {
        Toast.makeText(getHostActivity(), "error: " + reason, Toast.LENGTH_SHORT).show();
        btn_city.setText(getString(R.string.no_network));
    }

    @Override
    public void setWeatherInfo(Weather weather) {
        if(weather != null){
            weatherDataList = weather.getWeather_data();
            if(weatherDataList != null && weatherDataList.size() > 0 && weatherAdapter != null){
                weatherAdapter.setWeather(weather);
                weatherAdapter.setWeatherDataList(weatherDataList);
                weatherAdapter.notifyDataSetChanged();
                UmengUtil.get_weather(getHostActivity(), "url", weatherDataList.get(0).getWeather());
            }

            String city = weather.getCurrentCity();
            if(btn_city != null){
                btn_city.setText(Html.fromHtml("<u>" + city + "</u>"));
            }
        }

    }
}
