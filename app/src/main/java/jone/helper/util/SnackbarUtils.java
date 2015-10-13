package jone.helper.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarUtils {

    public static final int DURATION = Snackbar.LENGTH_LONG / 2 ;

    public static void show(View view, int message, int duration) {
        Snackbar.make(view, message, duration)
                .show();
    }

    public static void show(View view, int message) {
        show(view, message, Snackbar.LENGTH_SHORT);
    }

    public static void show(Activity activity, int message, int duration) {
        View view = activity.getWindow().getDecorView();
        show(view, message, duration);
    }

    public static void show(Activity activity, int message) {
        View view = activity.getWindow().getDecorView();
        show(view, message);
    }

    public static void showAction(View view, int message, int action,
                                  View.OnClickListener listener, int duration) {
        Snackbar.make(view, message, duration)
                .setAction(action, listener)
                .show();
    }


    public static void showAction(View view, int message, int action, View.OnClickListener listener) {
        showAction(view, message, action, listener, Snackbar.LENGTH_SHORT);
    }

    public static void showAction(Activity activity, int message, int action,
                                  View.OnClickListener listener, int duration) {
        View view = activity.getWindow().getDecorView();
        showAction(view, message, action, listener, duration);
    }

    public static void showAction(Activity activity, int message, int action, View.OnClickListener listener) {
        View view = activity.getWindow().getDecorView();
        showAction(view, message, action, listener);
    }
}
