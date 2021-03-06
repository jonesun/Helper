package jone.helper.util;

import androidx.annotation.IntDef;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import jone.helper.R;

/**
 * Created by Monkey on 2015/7/1.
 */
public class SnackbarUtil {
//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG})
//    public @interface SnackbarMode {}

    // android-support-design兼容包中新添加的一个类似Toast的控件。
    // make()中的第一个参数，可以写当前界面中的任意一个view对象。
    // 这样CoordinatorLayout就可以协调各个View之间的动画效果 Snackbar Snackbar.make(mCoordinatorLayout.getRootView(), "Snackbar", Snackbar.LENGTH_SHORT).show();
    private static Snackbar mSnackbar;

    public static void show(View view, String msg, @Snackbar.Duration int flag) {

        if (flag == 0) { // 短时显示
            mSnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        } else { // 长时显示
            mSnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        }

        mSnackbar.show();
        // Snackbar中有一个可点击的文字，这里设置点击所触发的操作。
        mSnackbar.setAction(R.string.close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Snackbar在点击“关闭”后消失
                mSnackbar.dismiss();
            }
        });
    }
}
