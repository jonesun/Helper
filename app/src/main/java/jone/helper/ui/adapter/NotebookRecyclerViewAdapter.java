package jone.helper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import jone.helper.R;
import jone.helper.bean.NotebookData;
import jone.helper.ui.activities.EditNotebookActivity;

/**
 * Created by jone.sun on 2015/9/29.
 */
public class NotebookRecyclerViewAdapter extends BaseRecyclerViewAdapter<NotebookRecyclerViewHolder, NotebookData> {

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
        NotebookData notebookData = getItem(position);
        holder.note_date.setText(notebookData.getDate());
        holder.note_content.setText(notebookData.getContent());
        holder.note_img_thumbtack.setImageResource(EditNotebookActivity.sThumbtackImgs[notebookData.getColor()]);
        holder.note_content.setBackgroundColor(EditNotebookActivity.sBackGrounds[notebookData.getColor()]);
        holder.note_title.setBackgroundColor(EditNotebookActivity.sTitleBackGrounds[notebookData.getColor()]);
    }
}
