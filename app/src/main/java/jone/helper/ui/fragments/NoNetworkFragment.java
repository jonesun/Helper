package jone.helper.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jone.helper.App;
import jone.helper.R;
import jone.helper.lib.util.SystemUtil;

/**
 * 无网络的fragment
 * Created by jone.sun on 2015/12/17.
 */
public class NoNetworkFragment extends Fragment {
    private TextView tvNoNetwork, tvRefresh;

    private RefreshListener refreshListener;
    private static final String TXT_NO_NETWORK = "txtNoNetwork";
    public NoNetworkFragment newInstance(String txtNoNetwork){
        NoNetworkFragment noNetworkFragment = new NoNetworkFragment();
        if(!TextUtils.isEmpty(txtNoNetwork)){
            Bundle bundle = new Bundle();
            bundle.putString(TXT_NO_NETWORK, txtNoNetwork);
            noNetworkFragment.setArguments(bundle);
        }
        return noNetworkFragment;
    }

    public NoNetworkFragment newInstance(){
        return newInstance(null);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_network, container, false);
        if(!(getActivity() instanceof RefreshListener)){
            throw new InflateException("activity need implements RefreshListener");
        }else {
            refreshListener = (RefreshListener) getActivity();
        }
        tvNoNetwork = (TextView) view.findViewById(R.id.tv_no_network);
        tvRefresh = (TextView) view.findViewById(R.id.tv_refresh);
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(TXT_NO_NETWORK)){
            tvNoNetwork.setText(bundle.getString(TXT_NO_NETWORK));
        }
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemUtil.hasNetWork(getActivity())){
                    refreshListener.refresh();
                }else {
                    App.showToast(getString(R.string.toast_please_check_network));
                }
            }
        });
        return view;
    }

    public interface RefreshListener {
        void refresh();
    }
}
