package jone.helper.lib.view;

import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by i on 13-11-3.
 */
public class CommonView {

    //在具有硬件菜单键设备上依然显示Action bar overflow
    public static void alwaysShowActionBarOverflow(Context c) {
        try {
            ViewConfiguration config = ViewConfiguration.get(c);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
            System.out.println(ex.getMessage());
        }
    }
}
