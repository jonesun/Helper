package jone.helper.ui.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.Arrays;

import jone.helper.R;
import jone.helper.ui.activities.HelperMainActivity;
import jone.helper.ui.adapter.ChooseThemeRecyclerViewAdapter;

/**
 * Created by jone.sun on 2015/11/23.
 */
public class ChooseThemeDialogFragment extends DialogFragment {
    private HelperMainActivity helperMainActivity;
    public ChooseThemeDialogFragment(HelperMainActivity helperMainActivity){
        this.helperMainActivity = helperMainActivity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_fragment_choose_theme, container);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ChooseThemeRecyclerViewAdapter chooseThemeRecyclerViewAdapter = new ChooseThemeRecyclerViewAdapter(helperMainActivity, this);

        String[] strings = getContext().getResources().getStringArray(R.array.pref_theme_setting_list_titles);

        chooseThemeRecyclerViewAdapter.setDataList(Arrays.asList(strings));
        recyclerView.setAdapter(chooseThemeRecyclerViewAdapter);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    dismiss();
                    return true; // pretend we've processed it
                } else{
                    return false; // pass on to be processed as normal
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getDialog().setCancelable(false);
//        Window window = getDialog().getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
////        WindowManager.LayoutParams lp = window.getAttributes();
////        lp.width = 613;
////        lp.height = 720;
////        window.setAttributes(lp);
    }
}
