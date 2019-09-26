package jone.helper.ui.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by jone.sun on 2015/7/2.
 */
public abstract class BaseFragment<T extends FragmentActivity> extends Fragment {
    private String TAG = "BaseFragment";
    private T hostActivity;
    private View rootView;
    private boolean hasRootView = false;

    private boolean showLifecycle = false;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        showLog("onAttach>>activity");
        this.hostActivity = (T) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showLog("onAttach>>context");
        if(context instanceof Activity){
            this.hostActivity = (T) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate");
        hostActivity = (T) getActivity();
    }

    protected abstract int getContentView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLog("onCreateView");
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
        showLog("onViewCreated");
        if(!hasRootView || refreshOnReCreateView()){
            findViews(view);
            initViews(view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showLog("onStart");
    }

    public void onResume() {
        super.onResume();
        showLog("onResume");
        if(hostActivity != null){
            MobclickAgent.onPageStart(hostActivity.getClass().getSimpleName()); //统计页面
        }
    }
    public void onPause() {
        super.onPause();
        showLog("onPause");
        if(hostActivity != null){
            MobclickAgent.onPageEnd(hostActivity.getClass().getSimpleName());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        showLog("onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showLog("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showLog("onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showLog("onDetach");
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

    public void setShowLifecycle(boolean showLifecycle) {
        this.showLifecycle = showLifecycle;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void showLog(String info){
        if(showLifecycle){
            Log.e(TAG, info);
        }
    }
}
