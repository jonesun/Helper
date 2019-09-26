package jone.helper.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jone.helper.R;
import jone.helper.ui.activities.base.BaseAppCompatWithLayoutActivity;

public class WeatherSelectCityActivity extends BaseAppCompatWithLayoutActivity {
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private TextView emptyTextView;
    @Override
    protected int getContentView() {
        return R.layout.activity_weather_select_city;
    }

    @Override
    protected void findViews() {
        setTitle(getString(R.string.title_activity_weather_select_city));
        emptyTextView = findView(android.R.id.empty);
        listView = findView(android.R.id.list);
        listView.setFastScrollEnabled(true);
        setData();
        if(arrayAdapter.getCount() == 0)
        {
            emptyTextView.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("result", arrayAdapter.getItem(position));
                WeatherSelectCityActivity.this.setResult(RESULT_OK, intent);
                WeatherSelectCityActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_weather_select_city, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
//                searchView.clearFocus();
                if(query.length()!=0){
                    listView.setFilterText(query);
                }else{
                    listView.clearTextFilter();
                }
                searchView.clearFocus();
                return false;
//                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()!=0){
                    listView.setFilterText(newText);
                }else{
                    listView.clearTextFilter();
                }
                return false;
            }
        });
//        searchView.expandActionView();
//        searchView.setSubmitButtonEnabled(true);
        searchView.requestFocus();
        return super.onCreateOptionsMenu(menu);
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
            listView.setAdapter(arrayAdapter);
            listView.setTextFilterEnabled(true);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("WeatherSelectCity", "", e);
        }finally {
            if(cursor != null){
                cursor.close();
            }
            if(database != null){
                database.close();
            }
        }
    }
}
