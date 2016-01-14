package jone.helper.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import jone.helper.R;

public final class GuideFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static GuideFragment newInstance(String content, boolean end, int color) {
        GuideFragment fragment = new GuideFragment();
        fragment.mContent = content;
        fragment.end = end;
        fragment.color = color;
        return fragment;
    }

    private String mContent = "???";
    private boolean end = false;
    private int color;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        view.setBackgroundColor(getResources().getColor(color));
        TextView text = (TextView) view.findViewById(R.id.text_info);
        text.setText(mContent);
//        text.setTextSize(16 * getResources().getDisplayMetrics().density);

        if(end){
            Button button = (Button) view.findViewById(R.id.btn_go_to);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.enter_alpha, R.anim.exit_alpha);
                }
            });
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
