package jone.helper.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jone.helper.R;

/**
 * Created by jone.sun on 2015/9/29.
 */
public class NotebookRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView note_content, note_date;
    public RelativeLayout note_title;
    public ImageView note_img_thumbtack;
    public NotebookRecyclerViewHolder(View itemView) {
        super(itemView);
        note_content = (TextView) itemView.findViewById(R.id.note_content);
        note_date = (TextView) itemView.findViewById(R.id.note_date);
        note_img_thumbtack = (ImageView) itemView.findViewById(R.id.note_img_thumbtack);
        note_title = (RelativeLayout) itemView.findViewById(R.id.note_title);
    }
}
