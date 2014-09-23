package jone.helper.ui.actionBarTabs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jone.helper.R;
import jone.helper.flashlight.FlashlightActivity;
import jone.helper.zxing.scan.CaptureActivity;

public class ActionBarTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private int position;

    private Button btnFlashlight, btnScan;
    public static ActionBarTabFragment newInstance(int position) {
        ActionBarTabFragment fragment = new ActionBarTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }
    public ActionBarTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_action_bar_tab, container, false);
        btnFlashlight = (Button) rootView.findViewById(R.id.btnFlashlight);
        btnScan = (Button) rootView.findViewById(R.id.btnScan);

        btnFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FlashlightActivity.class));
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CaptureActivity.class));
            }
        });
        return rootView;
    }
}
