package jone.helper.ui.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jone.helper.R;
import jone.helper.lib.util.Utils;
import jone.helper.ui.loader.CityCursorLoader;

/**
 * @author jone.sun on 2015/3/26.
 */
public class SelectCityActivity extends Activity {
    private static final String TAG = SelectCityActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {//把fragment整个视图装载到整个activity中去的写法。
            CursorLoaderListFragment list = new CursorLoaderListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    public static class CursorLoaderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {//模板类，这个尖角符号的作用我们看这个类的源码可知，在这个类里的一些回调函数中有一些数据的类型不确定用D代替，在具体使用的时候具体替换。

        private static final int LOADER_ID = 0;
        SimpleCursorAdapter mAdapter;
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setEmptyText("没有城市");// ListFragment提供的函数

            setHasOptionsMenu(true);// 说明这个fragment要回调onCreateOptionsMenu创建菜单

            mAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_single_choice,
                    null, new String[]{"name"}
                    ,new int[]{ android.R.id.text1}, 0);
            setListAdapter(mAdapter);
            setListShown(false);
            getListView().setFastScrollEnabled(true);
            getListView().setBackgroundColor(Color.BLACK);
            getListView().setTextFilterEnabled(true);
            getLoaderManager().initLoader(LOADER_ID, null, this);//因为这个类自身实现了LoaderManager.LoaderCallbacks接口，所以第三个参数是this.
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            Cursor cursor = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
            cursor.moveToPosition(position);
            Intent intent = new Intent();
            intent.putExtra("result", cursor.getString(cursor.getColumnIndex("name")));
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
            cursor.close();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {// 添加一个搜索菜单栏的代码
            super.onCreateOptionsMenu(menu, inflater);
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            SearchView search_view = new SearchView(getActivity());
//            search_view.setIconifiedByDefault(false);
//            search_view.requestFocus();
//            search_view.setFocusable(true);
            search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.length()!=0){
                        getListView().setFilterText(query);
                    }else{
                        getListView().clearTextFilter();
                    }
                    Log.e(TAG, "onQueryTextSubmit: " + query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.length()!=0){
                        getListView().setFilterText(newText);
                    }else{
                        getListView().clearTextFilter();
                    }
                    getLoaderManager().restartLoader(LOADER_ID, null, CursorLoaderListFragment.this);
                    Log.e(TAG, "onQueryTextChange: " + newText);
                    return false;
                }

            });
            search_view.setSubmitButtonEnabled(true);
            item.setActionView(search_view);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Create a new CursorLoader with the following query parameters.
            return new CityCursorLoader(getActivity().getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // A switch-case is useful when dealing with multiple Loaders/IDs
            switch (loader.getId()) {
                case LOADER_ID:
                    // The asynchronous load is complete and the data
                    // is now available for use. Only now can we associate
                    // the queried Cursor with the SimpleCursorAdapter.
                    mAdapter.swapCursor(cursor);
                    if (isResumed()) {
                        setListShown(true);
                    } else {
                        setListShownNoAnimation(true);
                    }
                    getListView().setTextFilterEnabled(true);
                    break;
            }
            // The listview now displays the queried data.
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            // For whatever reason, the Loader's data is now unavailable.
            // Remove any references to the old data by replacing it with
            // a null Cursor.
            mAdapter.swapCursor(null);// 确保onLoadFinished结束后我们不再使用这个Cursor。
        }
    }
}
