package jone.helper.ui.loader;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class CustomV4ListAsyncTaskLoader<T> extends AsyncTaskLoader<List<T>> {
    private List<T> list;
    private LoadListener<T> listener;
    public CustomV4ListAsyncTaskLoader(Context context, LoadListener<T> listener) {
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
    public List<T> loadInBackground() {
        // even if fail return empty list and print exception stack trace “只读的”
//        list = Collections.unmodifiableList(listener.loading());
        list = listener.loading();
        return list;
    }

    public interface LoadListener<T> {
        List<T> loading();
    }
}

