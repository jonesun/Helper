package jone.helper.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jone.sun on 2015/7/2.
 */
public abstract class BaseFragment<T extends FragmentActivity> extends Fragment {

    private T hostActivity;

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
        return inflater.inflate(getContentView(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews(view);
    }

    protected abstract void findViews(View view);

    protected void initViews(View view){}

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public T getHostActivity() {
        return hostActivity;
    }
}
