package jone.helper.lib.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jone.sun on 2016/1/5.
 */
public class ViewUtil {
    /**
     * Simpler version of {@link View#findViewById(int)} which infers the target
     * type.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends View> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * Simpler version of {@link Activity#findViewById(int)} which infers the
     * target type.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends View> T findById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static <T extends View> T findById(Object activityOrView, int id) {
        if (activityOrView instanceof Activity) {
            return findById((Activity) activityOrView, id);
        }
        if (activityOrView instanceof View) {
            return findById((View) activityOrView, id);
        }
        throw new IllegalArgumentException("activityOrView must be instance of Activity or View.");
    }


    public static <T extends View> T inflate(Context context, int id, ViewGroup root,
                                             boolean attachToRoot) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (T) inflater.inflate(id, root, attachToRoot);
    }

    public static <T extends View> T inflate(Context context, int id, ViewGroup root) {
        return inflate(context, id, root, root != null);
    }

    public static <T extends View> T inflate(Context context, int id) {
        return inflate(context, id, null);
    }

    public static <T extends View> T inflate(LayoutInflater inflater, int id, ViewGroup root,
                                             boolean attachToRoot) {
        return (T) inflater.inflate(id, root, attachToRoot);
    }

    public static <T extends View> T inflate(LayoutInflater inflater, int id, ViewGroup root) {
        return inflate(inflater, id, root, root != null);
    }

    public static <T extends View> T inflate(LayoutInflater inflater, int id) {
        return inflate(inflater, id, null);
    }


    public static List<String> getDrawableState(View v) {
        int[] states = v.getDrawableState();

        List<String> stateList = new ArrayList<>();
        for (int i : states) {
            switch (i) {
                case android.R.attr.state_pressed:
                    stateList.add("state_pressed");
                    break;
                case android.R.attr.state_window_focused:
                    stateList.add("state_window_focused");
                    break;
                case android.R.attr.state_focused:
                    stateList.add("state_focused");
                    break;
                case android.R.attr.state_selected:
                    stateList.add("state_selected");
                    break;
                case android.R.attr.state_activated:
                    stateList.add("state_activated");
                    break;
                case android.R.attr.state_active:
                    stateList.add("state_active");
                    break;
                case android.R.attr.state_enabled:
                    stateList.add("state_enabled");
                    break;
                default:
                    stateList.add(i + "");
            }
        }
        return stateList;
    }
}
