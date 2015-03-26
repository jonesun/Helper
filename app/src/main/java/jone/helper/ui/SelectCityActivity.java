package jone.helper.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jone.helper.R;
import jone.helper.lib.util.Utils;

/**
 * @author jone.sun on 2015/3/26.
 */
public class SelectCityActivity extends ListActivity{
    private static final String TAG = SelectCityActivity.class.getSimpleName();
    ArrayAdapter<String> arrayAdapter;
    private SearchView search_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setScreenOrientation(this);
        setContentView(R.layout.select_city_activity);
        android.app.ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        search_view = (SearchView) findViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()!=0){
                    getListView().setFilterText(query);
                }else{
                    getListView().clearTextFilter();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()!=0){
                    getListView().setFilterText(newText);
                }else{
                    getListView().clearTextFilter();
                }
                return false;
            }

        });
        search_view.setSubmitButtonEnabled(true);
        setData();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent();
        intent.putExtra("result", arrayAdapter.getItem(position));
        SelectCityActivity.this.setResult(RESULT_OK, intent);
        SelectCityActivity.this.finish();
    }

    private void setData(){
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String packageNames = info.packageName;
            String DB_PATH = "/data/data/" + packageNames + "/databases/";
            String DB_NAME = "weather_city.db";
            if (!new File(DB_PATH + DB_NAME).exists()) {
                File f = new File(DB_PATH);
                if (!f.exists()) {
                    f.mkdir();
                }
                try {
                    InputStream is = getAssets().open(DB_NAME);
                    OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
            cursor = database.query("weathercity", new String[]{"id as _id", "name"}, null, null, null, null, null);
            ArrayList<String> list = new ArrayList<>();
            if(cursor != null){
                while (cursor.moveToNext()){
                    list.add(cursor.getString(1));
                }
            }
            arrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_single_choice,
                    list);
            setListAdapter(arrayAdapter);
            getListView().setTextFilterEnabled(true);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
        }finally {
            if(cursor != null){
                cursor.close();
            }
            if(database != null){
                database.close();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
