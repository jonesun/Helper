package jone.helper.ui.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jone.helper.R;
import jone.helper.lib.ormlite.NotebookDao;
import jone.helper.lib.ormlite.entities.NotebookData;
import jone.helper.lib.util.GsonUtils;
import jone.helper.lib.util.StringUtils;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.activities.base.BaseFragmentActivity;
import jone.helper.ui.adapter.NotebookAdapter;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookActivity extends BaseAppCompatActivity {
    private NotebookAdapter adapter;
    private GridView gridView;
    @Override
    protected int getContentView() {
        return R.layout.activity_notebook;
    }

    @Override
    protected void findViews() {
        BaseFragmentActivity.setStatusBarView(this, getResources().getColor(R.color.jone_style_blue_700));
        initToolbar();
        gridView = findView(R.id.gridView);
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBarTextView.setText("便签");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notebook_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                startActivity(new Intent(NotebookActivity.this, EditNotebookActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void initViews() {
        super.initViews();
        adapter = new NotebookAdapter(NotebookActivity.this);
        gridView.setAdapter(adapter);
        adapter.setData(NotebookDao.getInstance(NotebookActivity.this).queryList());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NotebookData notebookData = (NotebookData) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(NotebookActivity.this, EditNotebookActivity.class);
                intent.putExtra("notebookData", notebookData);
                startActivity(intent);
                finish();
            }
        });
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                adapter.checkSelect(actionMode, i, b);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_notebook_list, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        List<NotebookData> notebookDatas = adapter.getSelectDeleteData();
                        if (notebookDatas.size() > 0) {
                            for (NotebookData notebookData : notebookDatas) {
                                NotebookDao.getInstance(NotebookActivity.this).delete(notebookData);
                            }
                            adapter.setData(NotebookDao.getInstance(NotebookActivity.this).queryList());
                        }
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                adapter.unSelectAll(actionMode);
            }
        });
    }
}
