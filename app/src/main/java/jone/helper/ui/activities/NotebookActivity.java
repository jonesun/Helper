package jone.helper.ui.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import jone.helper.lib.util.SystemUtil;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.activities.base.BaseFragmentActivity;
import jone.helper.ui.adapter.AppsRecyclerViewAdapter;
import jone.helper.ui.adapter.NotebookAdapter;
import jone.helper.ui.adapter.NotebookRecyclerViewAdapter;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookActivity extends BaseAppCompatActivity implements AppsRecyclerViewAdapter.OnItemClickListener {
    private NotebookRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected int getContentView() {
        return R.layout.activity_notebook;
    }

    @Override
    protected void findViews() {
        BaseFragmentActivity.setStatusBarView(this, getResources().getColor(R.color.jone_style_blue_700));
        initToolbar();
        recyclerView = findView(R.id.recyclerView);
    }

    @Override
    protected void initViews() {
        super.initViews();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        adapter = new NotebookRecyclerViewAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter.setDataList(NotebookDao.getInstance(this).queryList());
    }

    @Override
    public void onItemClick(View view, int position) {
        NotebookData notebookData = adapter.getItem(position);
        Intent intent = new Intent(NotebookActivity.this, EditNotebookActivity.class);
        intent.putExtra("notebookData", notebookData);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemLongClick(final View view, final int position) {
        final CardView cardView = (CardView) view;
        cardView.setCardBackgroundColor(Color.RED);
        Snackbar snackbar = Snackbar.make(view, "便签", Snackbar.LENGTH_LONG)
                .setAction("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NotebookDao.getInstance(NotebookActivity.this).delete(adapter.getItem(position));
                        adapter.setDataList(NotebookDao.getInstance(NotebookActivity.this).queryList());
                    }
                });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }
        });
        snackbar.show();
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
        mToolBarTextView.setText(getString(R.string.menu_item_note));
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
}
