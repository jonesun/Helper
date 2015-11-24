package jone.helper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jone.helper.R;
import jone.helper.ui.activities.HelperMainActivity;

/**
 * Created by jone.sun on 2015/11/23.
 */
public class ChooseThemeRecyclerViewAdapter extends BaseRecyclerViewAdapter<ChooseThemeRecyclerViewAdapter.ChooseThemeRecyclerViewHolder, String> {
    private HelperMainActivity helperMainActivity;
    private DialogFragment dialogFragment;
    public ChooseThemeRecyclerViewAdapter(HelperMainActivity helperMainActivity, DialogFragment dialogFragment) {
        super(helperMainActivity);
        this.helperMainActivity = helperMainActivity;
        this.dialogFragment = dialogFragment;
    }

    @Override
    public ChooseThemeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false);
        ChooseThemeRecyclerViewHolder mViewHolder = new ChooseThemeRecyclerViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ChooseThemeRecyclerViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        String themeName = getItem(position);
        holder.tv_label.setText(themeName);
        holder.tv_label.setBackgroundColor(getContext().getResources().getColor(getContext().getResources().
                getIdentifier(themeName + "_500", "color", getContext().getPackageName())));
        holder.tv_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogFragment != null && helperMainActivity != null){
                    helperMainActivity.onTheme(position);
                    dialogFragment.dismiss();
                }
            }
        });
    }

    public class ChooseThemeRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_label;

        public ChooseThemeRecyclerViewHolder(View itemView) {
            super(itemView);
            tv_label = (TextView) itemView.findViewById(R.id.tv_label);
        }
    }
}

