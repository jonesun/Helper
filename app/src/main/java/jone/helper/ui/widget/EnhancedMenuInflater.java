package jone.helper.ui.widget;

import android.support.annotation.MenuRes;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Created by jone.sun on 2016/1/22.
 */
public class EnhancedMenuInflater {
    public static void inflate(@MenuRes int menuRes, MenuInflater inflater, Menu menu, boolean forceVisible) {
        inflater.inflate(menuRes, menu);

        if (!forceVisible) {
            return;
        }

        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            // check if app:showAsAction = "ifRoom"
            if (((MenuItemImpl) item).requestsActionButton()) {
                item.setShowAsAction(SupportMenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
    }
}
