package jone.helper.ui.fragments.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jone.sun on 2015/10/19.
 */
public abstract class DataBindingBaseFragment<T extends FragmentActivity, V extends ViewDataBinding> extends Fragment {

    private T hostActivity;
    private V viewDataBinding;
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
        viewDataBinding = DataBindingUtil.inflate(inflater, getContentView(), container, false);
        return viewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(viewDataBinding);
    }

    public void initViews(V viewDataBinding){}


    public T getHostActivity() {
        return hostActivity;
    }

    public V getViewDataBinding() {
        return viewDataBinding;
    }
}
