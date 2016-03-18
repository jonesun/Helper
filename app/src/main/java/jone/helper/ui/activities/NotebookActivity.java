package jone.helper.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import java.util.List;

import jone.helper.R;
import jone.helper.dao.NotebookBeanDao;
import jone.helper.bean.Notebook;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.adapter.AppsRecyclerViewAdapter;
import jone.helper.ui.adapter.NotebookRecyclerViewAdapter;

/**
 * Created by jone.sun on 2015/9/7.
 */
public class NotebookActivity extends BaseAppCompatActivity implements AppsRecyclerViewAdapter.OnItemClickListener {
    private NotebookRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public static final int requestCode_add = 0;
    public static final int requestCode_edit = 1;

    @Override
    protected int getContentView() {
        return R.layout.activity_notebook;
    }

    @Override
    protected void findViews() {
        initToolbar();
        recyclerView = findView(R.id.recyclerView);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        adapter = new NotebookRecyclerViewAdapter(NotebookActivity.this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);
        List<Notebook> notebookDataList = NotebookBeanDao.getInstance(this).queryList();
        adapter.setDataList(notebookDataList);
        if (notebookDataList.size() == 0) {
            setNoData();
        }
    }


    private void setNoData() {
        ViewStub stub = (ViewStub) findViewById(R.id.vs_no_notebook);
        stub.inflate();
    }

    @Override
    public void onItemClick(View view, int position) {
        Notebook notebookData = adapter.getItem(position);
        Intent intent = new Intent(NotebookActivity.this, EditNotebookActivity.class);
        intent.putExtra("notebookData", notebookData);
        startActivityForResult(intent, requestCode_edit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ssss", "onActivityResult: " + requestCode);
        if (resultCode == RESULT_OK) {
            if (adapter != null) {
                List<Notebook> notebookDataList = NotebookBeanDao.getInstance(this).queryList();
                adapter.setDataList(notebookDataList);
            }
            Log.e("ssss", "onActivityResult updateView");
        }

    }

    @Override
    public void onItemLongClick(final View view, final int position) {
        final CardView cardView = (CardView) view;
        cardView.setCardBackgroundColor(Color.RED);
        Snackbar snackbar = Snackbar.make(view, getString(R.string.menu_item_note), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.delete), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NotebookBeanDao.getInstance(NotebookActivity.this).delete(adapter.getItem(position));
                        adapter.removeData(position);
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
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        mToolbar.setTitle("");
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
        tv_title.setText(getString(R.string.menu_item_note));
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
                startActivityForResult(new Intent(NotebookActivity.this,
                        EditNotebookActivity.class), requestCode_add);
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
