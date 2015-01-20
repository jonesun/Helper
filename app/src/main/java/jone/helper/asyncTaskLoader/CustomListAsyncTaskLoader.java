package jone.helper.asyncTaskLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.Collections;
import java.util.List;

public class CustomListAsyncTaskLoader extends AsyncTaskLoader<List> {
    private List list;
    private LoadListener listener;
    public CustomListAsyncTaskLoader(Context context, LoadListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onStartLoading() {
        // just make sure if we already have content to deliver
        if (list != null){
            deliverResult(list);
        }

        // otherwise if something has been changed or first try
        if(takeContentChanged() || list == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        // clear reference to object
        // it's necessary to allow GC to collect the object
        // to avoid memory leaking
        list = null;
    }

    @Override
    public List loadInBackground() {
        // even if fail return empty list and print exception stack trace
        list = Collections.unmodifiableList(listener.loading());
        return list;
    }

    public interface LoadListener {
        List loading();
    }
}

