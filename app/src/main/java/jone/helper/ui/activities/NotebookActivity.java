package jone.helper.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import java.util.List;

import jone.helper.R;
import jone.helper.bean.NotebookData;
import jone.helper.dao.NotebookDao;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.ui.adapter.AppsRecyclerViewAdapter;
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
        List<NotebookData> notebookDatas = NotebookDao.getInstance(this).queryList();
        adapter.setDataList(notebookDatas);
        if(notebookDatas.size() == 0){
            setNoData();
        }
    }

    private void setNoData(){
        ViewStub stub = (ViewStub) findViewById(R.id.vs_no_notebook);
        stub.inflate();
        TextView text = (TextView) findViewById(R.id.tv_no_data);
        text.setText("您还没有写过便签");
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
