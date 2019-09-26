package jone.helper.ui.widget;

import android.app.ActionBar;
import android.content.Context;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jone.sun on 2016/1/22.
 */
public class SplitToolbar extends Toolbar {
    public SplitToolbar(Context context) {
        super(context);
    }

    public SplitToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplitToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof ActionMenuView) {
            params.width = ActionBar.LayoutParams.MATCH_PARENT;
        }
        super.addView(child, params);
    }
}
