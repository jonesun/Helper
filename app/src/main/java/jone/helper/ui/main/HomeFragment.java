package jone.helper.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jone.helper.Constants;
import jone.helper.R;
import jone.helper.adapter.NewsAdapter;
import jone.helper.asyncTaskLoader.CustomV4ListAsyncTaskLoader;
import jone.helper.bean.News;
import jone.helper.lib.processDataOperator.ProcessDBDataOperator;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.Utils;
import jone.helper.util.DownloadHtmlFrom36krUtil;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int loader_id = 0001;
    private RecyclerView mRecyclerView;
    private TextView txt_welcome;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();
    private LoaderManager loaderManager;
    private MenuActivity activity;
    private static HomeFragment instance = null;
    public static HomeFragment getInstance(){
        if(instance == null){
            instance = new HomeFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = getLoaderManager();
        activity = (MenuActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_welcome = (TextView) view.findViewById(R.id.txt_welcome);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        String json = ProcessDBDataOperator.getInstance(activity).getValueByKey(Constants.KEY_NEWS,
                null);
        if(GsonUtils.isGoodJson(json)){
            newsList = GsonUtils.getGson().fromJson(json, new TypeToken<List<News>>() {
            }.getType());
        }
        if(newsList == null || newsList.size() == 0){
            txt_welcome.setVisibility(View.VISIBLE);
        }else {
            txt_welcome.setVisibility(View.GONE);
        }
        newsAdapter = new NewsAdapter(activity, newsList);
        mRecyclerView.setAdapter(newsAdapter);

        if(Utils.isNetworkAlive(activity)){
            loaderManager.initLoader(loader_id, null, loaderCallbacks);
        }

    }

    private LoaderManager.LoaderCallbacks<List> loaderCallbacks = new LoaderManager.LoaderCallbacks<List>() {

        @Override
        public Loader<List> onCreateLoader(int i, final Bundle bundle) {
            return new CustomV4ListAsyncTaskLoader(activity, new CustomV4ListAsyncTaskLoader.LoadListener() {
                @Override
                public List loading() {
                    return getNewsList();
                }
            });
        }

        @Override
        public void onLoadFinished(Loader<List> listLoader, List list) {
            if(list.size() != 0){
                newsList.clear();
                newsList.addAll(list);
                newsAdapter.setNewsList(newsList);
                newsAdapter.notifyDataSetChanged();
            }
            if(newsList == null || newsList.size() == 0){
                txt_welcome.setVisibility(View.VISIBLE);
                txt_welcome.setText("欢迎来到帮手的世界");
            }else {
                txt_welcome.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List> listLoader) {
            newsList.clear();
        }
    };

    public List<News> getNewsList() {
        List<News> newses = new ArrayList<>();
        try{
            DownloadHtmlFrom36krUtil gcp = new DownloadHtmlFrom36krUtil();
            newses = gcp.get36krNews();
            if(newses != null && newses.size() > 0){
                ProcessDBDataOperator.getInstance(getActivity()).delValueByKey(Constants.KEY_NEWS);
                ProcessDBDataOperator.getInstance(getActivity()).putValue(Constants.KEY_NEWS, GsonUtils.toJson(newses));
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }
        return newses;
    }
}
