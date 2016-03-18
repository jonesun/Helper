package jone.helper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jone.helper.R;
import jone.helper.bean.Notebook;
import jone.helper.ui.activities.EditNotebookActivity;

/**
 * Created by jone.sun on 2015/9/29.
 */
public class NotebookRecyclerViewAdapter extends BaseRecyclerViewAdapter<NotebookRecyclerViewHolder, Notebook> {

    public NotebookRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public NotebookRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotebookRecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notebook_list, parent, false));
    }

    @Override
    public void onBindViewHolder(NotebookRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Notebook notebookData = getItem(position);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        holder.note_date.setText(format.format(new Date(notebookData.getCreateDate())));
        holder.note_content.setText(notebookData.getContent());
        holder.note_img_thumbtack.setImageResource(EditNotebookActivity.sThumbtackImgs[notebookData.getColor()]);
        holder.note_content.setBackgroundColor(EditNotebookActivity.sBackGrounds[notebookData.getColor()]);
        holder.note_title.setBackgroundColor(EditNotebookActivity.sTitleBackGrounds[notebookData.getColor()]);
    }
}
