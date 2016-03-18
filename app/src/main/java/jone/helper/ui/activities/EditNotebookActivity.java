package jone.helper.ui.activities;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jone.helper.R;
import jone.helper.dao.NotebookBeanDao;
import jone.helper.bean.Notebook;
import jone.helper.ui.activities.base.BaseAppCompatActivity;
import jone.helper.util.KJAnimations;

/**
 * Created by jone.sun on 2015/9/2.
 */
public class EditNotebookActivity extends BaseAppCompatActivity implements View.OnTouchListener, View.OnClickListener{
    private Toolbar mToolbar;
    EditText mEtContent;

    TextView mTvDate;

    RelativeLayout mLayoutTitle, mLayoutMenu;

    ImageView mImgThumbtack, mImgMenu,
            mImgGreen, mImgBlue, mImgPurple, mImgYellow, mImgRed;

    private Notebook notebookData;

    public static final int[] sBackGrounds = { 0xffe5fce8,// 绿色
            0xfffffdd7,// 黄色
            0xffffddde,// 红色
            0xffccf2fd,// 蓝色
            0xfff7f5f6,// 紫色
    };
    public static final int[] sTitleBackGrounds = { 0xffcef3d4,// 绿色
            0xffebe5a9,// 黄色
            0xffecc4c3,// 红色
            0xffa9d5e2,// 蓝色
            0xffddd7d9,// 紫色
    };

    public static final int[] sThumbtackImgs = { R.mipmap.green,
            R.mipmap.yellow, R.mipmap.red, R.mipmap.blue,
            R.mipmap.purple };

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_notebook;
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
        tv_title.setText("便签");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_notebook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                notebookData.setContent(mEtContent.getText().toString());
                NotebookBeanDao.getInstance(EditNotebookActivity.this).insertOrReplace(notebookData);
                Toast.makeText(EditNotebookActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void findViews(){
        initToolbar();
        mEtContent = (EditText) findViewById(R.id.note_detail_edit);
        mTvDate = (TextView) findViewById(R.id.note_detail_tv_date);
        mLayoutTitle = (RelativeLayout) findViewById(R.id.note_detail_titlebar);
        mLayoutMenu = (RelativeLayout) findViewById(R.id.note_detail_menu);
        mImgThumbtack = (ImageView) findViewById(R.id.note_detail_img_thumbtack);

        mImgMenu = (ImageView) findViewById(R.id.note_detail_img_button);
        mImgGreen = (ImageView) findViewById(R.id.note_detail_img_green);
        mImgBlue = (ImageView) findViewById(R.id.note_detail_img_blue);
        mImgPurple = (ImageView) findViewById(R.id.note_detail_img_purple);
        mImgYellow = (ImageView) findViewById(R.id.note_detail_img_yellow);
        mImgRed = (ImageView) findViewById(R.id.note_detail_img_red);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_detail_img_green:
                notebookData.setColor(0);
                mToolbar.setBackgroundColor(sTitleBackGrounds[0]);
                getThemeTool().setStatusBarView(this, sTitleBackGrounds[0]);
                break;
            case R.id.note_detail_img_blue:
                notebookData.setColor(3);
                mToolbar.setBackgroundColor(sTitleBackGrounds[4]);
                getThemeTool().setStatusBarView(this, sTitleBackGrounds[3]);
                break;
            case R.id.note_detail_img_purple:
                notebookData.setColor(4);
                mToolbar.setBackgroundColor(sTitleBackGrounds[4]);
                getThemeTool().setStatusBarView(this, sTitleBackGrounds[4]);
                break;
            case R.id.note_detail_img_yellow:
                notebookData.setColor(1);
                mToolbar.setBackgroundColor(sTitleBackGrounds[1]);
                getThemeTool().setStatusBarView(this, sTitleBackGrounds[1]);
                break;
            case R.id.note_detail_img_red:
                notebookData.setColor(2);
                mToolbar.setBackgroundColor(sTitleBackGrounds[2]);
                getThemeTool().setStatusBarView(this, sTitleBackGrounds[2]);
                break;
        }
        mImgThumbtack.setImageResource(sThumbtackImgs[notebookData.getColor()]);
        mEtContent.setBackgroundColor(sBackGrounds[notebookData.getColor()]);
        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[notebookData.getColor()]);
        closeMenu();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mLayoutMenu.getVisibility() == View.GONE) {
                openMenu();
            } else {
                closeMenu();
            }
        }
        return true;
    }

    public void initData() {
        if(getIntent().hasExtra("notebookData")){
            notebookData = (Notebook) getIntent().getSerializableExtra("notebookData");
        }
        if (notebookData == null) {
            notebookData = new Notebook();
            notebookData.setContent("");
            notebookData.setColor(3);
        }
        if (notebookData.getCreateDate() == null || notebookData.getCreateDate() == 0L) {
            notebookData.setCreateDate(System.currentTimeMillis());
        }
    }

    @Override
    public void initViews() {
        initData();
        mImgGreen.setOnClickListener(this);
        mImgBlue.setOnClickListener(this);
        mImgPurple.setOnClickListener(this);
        mImgYellow.setOnClickListener(this);
        mImgRed.setOnClickListener(this);

        mEtContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEtContent.setSingleLine(false);
        mEtContent.setHorizontallyScrolling(false);
        mEtContent.setText(Html.fromHtml(notebookData.getContent()).toString());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mTvDate.setText(format.format(new Date(notebookData.getCreateDate())));

        mToolbar.setBackgroundColor(sTitleBackGrounds[notebookData.getColor()]);
        getThemeTool().setStatusBarView(this, sTitleBackGrounds[notebookData.getColor()]);

        mEtContent.setBackgroundColor(sBackGrounds[notebookData.getColor()]);
        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[notebookData.getColor()]);
        mImgThumbtack.setImageResource(sThumbtackImgs[notebookData.getColor()]);

        mImgMenu.setOnTouchListener(this);
        mLayoutMenu.setOnTouchListener(this);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void openMenu() {
        KJAnimations.openAnimation(mLayoutMenu, mImgMenu, 500);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void closeMenu() {
        KJAnimations.closeAnimation(mLayoutMenu, mImgMenu, 500);
    }

}
