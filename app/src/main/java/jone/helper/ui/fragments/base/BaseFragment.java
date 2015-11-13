package jone.helper.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by jone.sun on 2015/7/2.
 */
public abstract class BaseFragment<T extends FragmentActivity> extends Fragment {

    private T hostActivity;
    private View rootView;
    private boolean hasRootView = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostActivity = (T) getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.hostActivity = (T) activity;
    }

    protected abstract int getContentView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(getContentView(), container, false);
            hasRootView = false;
        } else {
            hasRootView = true;
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!hasRootView || refreshOnReCreateView()){
            findViews(view);
            initViews(view);
        }
        if(!hasRootView){
            findViews(view);
            initViews(view);
        }
    }

    protected abstract void findViews(View view);

    protected void initViews(View view){}

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public T getHostActivity() {
        return hostActivity;
    }

    /***
     * 再次到onCreateView时，是否刷新view
     * @return
     */
    public boolean refreshOnReCreateView(){
        return false;
    }

    public void onResume() {
        super.onResume();
        if(hostActivity != null){
            MobclickAgent.onPageStart(hostActivity.getClass().getSimpleName()); //统计页面
        }
    }
    public void onPause() {
        super.onPause();
        if(hostActivity != null){
            MobclickAgent.onPageEnd(hostActivity.getClass().getSimpleName());
        }
    }
}
