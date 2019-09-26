package jone.helper.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jone.helper.App;
import jone.helper.R;
import jone.helper.bean.News;
import jone.helper.lib.model.network.NetworkRequest;
import jone.helper.lib.model.network.ResponseCallback;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.Utils;
import jone.helper.ui.adapter.NewsAdapter;

public class EggsActivity extends FragmentActivity {
    private static final String TAG = EggsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setScreenOrientation(this);
        setContentView(R.layout.activity_eggs);
        android.app.ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, EggsFragment.getInstance())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class EggsFragment extends Fragment {
        private static final String TAG = EggsFragment.class.getSimpleName();
        private static final int loader_id = 0001;
        private SwipeRefreshLayout swipe_refresh_widget;
        private RecyclerView mRecyclerView;
        private TextView txt_welcome;
        private NewsAdapter newsAdapter;
        private LinearLayout layout_ad;
        private EggsActivity activity;
        private static EggsFragment instance = null;
        public static EggsFragment getInstance(){
            if(instance == null){
                instance = new EggsFragment();
            }
            return instance;
        }
        private static final int WHAT_GET_DONE = 100001;
        private Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case WHAT_GET_DONE:
                        if(msg.obj != null && msg.obj instanceof List){
                            List<News> newses = (List<News>) msg.obj;
                            if(newses.size() == 0){
                                txt_welcome.setVisibility(View.VISIBLE);
                                txt_welcome.setText("欢迎来到帮手的世界");
                            }else {
                                newsAdapter.setNewsList(newses);
                                newsAdapter.notifyDataSetChanged();
                                txt_welcome.setVisibility(View.GONE);
                            }
                        }else {
                            txt_welcome.setVisibility(View.VISIBLE);
                            txt_welcome.setText("欢迎来到帮手的世界");
                        }
                        break;
                }
            }
        };
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (EggsActivity) getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_eggs, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            txt_welcome = (TextView) view.findViewById(R.id.txt_welcome);
            swipe_refresh_widget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

            swipe_refresh_widget.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setHasFixedSize(true);

            newsAdapter = new NewsAdapter(activity, new ArrayList<News>());
            mRecyclerView.setAdapter(newsAdapter);
            if(Utils.isNetworkAlive(activity)){
                getNewsList();
            }else {
                handler.sendMessage(handler.obtainMessage(WHAT_GET_DONE, null));
            }

            layout_ad = (LinearLayout) view.findViewById(R.id.layout_ad);

            newsAdapter.setOnItemClickListener(new NewsAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onClick(View view, News news) {
                    Intent intent = new Intent(activity, NewsDetailActivity.class);
                    intent.putExtra("url", news.getUrl());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });
            swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getNewsList();
                    swipe_refresh_widget.setRefreshing(false);
                }
            });
        }

        /***
         * 新闻类别     col          取值(90:国内,91:国际,92:社会,94:体育,95:娱乐,93:军事,96:科技,97:财经,98:股市,99:美股)
         * 新闻形式     type       取值(3:视频,2:图片,空:全部)
         * 新闻条数     num
         * http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=90,91&type=2&num=5
         */
        private void getNewsList() {
            //
            NetworkRequest request = new NetworkRequest.Builder()
                    .get()
                    .url("http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?num=20")
                    .setUsingCacheWithNoNetwork(true).build();
            App.getVolleyNetworkOperator().request(request, String.class, new ResponseCallback<String>() {
                @Override
                public void onSuccess(String response, boolean fromCache) {
                    List<News> newses = new ArrayList<>();
                    if(response != null && response.length() > 0){
                        response = response.replace("var jsonData = ", "").replace(";", "");
                        Log.e(TAG, "response: " + response);
                        if(GsonUtils.isGoodJson(response)){
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.length() > 0 && jsonObject.has("list")){
                                    String listStr = jsonObject.getString("list");
                                    Log.e(TAG, "list: " + listStr);
                                    JSONArray jsonArray = new JSONArray(listStr);
                                    if(jsonArray.length() > 0){
                                        for(int i = 0; i < jsonArray.length(); i++){
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            News news = new News();
                                            if(jsonObject1.has("title")){
                                                news.setTitle(jsonObject1.getString("title"));
                                            }
                                            if(jsonObject1.has("url")){
                                                news.setUrl(jsonObject1.getString("url"));
                                            }
                                            if(jsonObject1.has("pic")){
                                                news.setImageUrl(jsonObject1.getString("pic"));
                                            }
                                            if(jsonObject1.has("time")){
                                                news.setTime(jsonObject1.getLong("time"));
                                            }
                                            news.setFrom("sina");
                                            newses.add(news);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "", e);
                            }
                        }
                    }
                    handler.sendMessage(handler.obtainMessage(WHAT_GET_DONE, newses));
                }

                @Override
                public void onFailure(Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_GET_DONE, null));
                }
            });
        }
    }
}
