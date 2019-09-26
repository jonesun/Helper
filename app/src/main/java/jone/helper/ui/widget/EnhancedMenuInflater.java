package jone.helper.ui.widget;

import androidx.annotation.MenuRes;
import androidx.core.internal.view.SupportMenuItem;
import androidx.appcompat.view.menu.MenuItemImpl;
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
