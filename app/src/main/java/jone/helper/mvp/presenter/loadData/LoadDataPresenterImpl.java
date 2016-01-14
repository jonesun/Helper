package jone.helper.mvp.presenter.loadData;

import java.util.List;
import jone.helper.mvp.model.load.Callback;
import jone.helper.mvp.model.load.LoadDataModel;
import jone.helper.mvp.view.loadData.LoadDataView;

/**
 * Created by jone.sun on 2016/1/13.
 */
public abstract class LoadDataPresenterImpl<T, R> implements LoadDataPresenter, Callback<List<T>> {
    private static final String TAG = "LoadDataPresenterImpl";
    private LoadDataModel<T, R> loadDataModel;
    private LoadDataView<T> loadDataView;
    private static final int DEFAULT_FIRST_PAGE = 1;
    private int pageIndex = DEFAULT_FIRST_PAGE; //默认从第一页开始

    public LoadDataPresenterImpl(LoadDataView<T> loadDataView){
        this.loadDataModel = getLoadDataModel();
        this.loadDataView = loadDataView;
    }

    @Override
    public void refresh() {
        pageIndex = DEFAULT_FIRST_PAGE;
        loadList();
    }

    @Override
    public void loadList() {
        pageIndex = DEFAULT_FIRST_PAGE;
        loadDataView.showProgress();
        loadDataModel.loadData(pageIndex, this);
    }

    @Override
    public void loadMore() {
        pageIndex++;
        loadDataView.showProgress();
        loadDataModel.loadData(pageIndex, this);
    }


    @Override
    public void onComplete(int resultCode, String message, List<T> data) {
        if(data != null && data.size() > 0){
            loadDataView.addDataList(data);
            if(resultCode == RESULT_CODE_NO_NEXT){
                loadDataView.showLoadFailMsg(LoadDataView.REASON_OF_NO_NEXT, message);
            }
        }else {
            pageIndex--; //加载失败 回滚
            if(resultCode == RESULT_CODE_NO_NETWORK){
                loadDataView.showLoadFailMsg(LoadDataView.REASON_OF_NO_NETWORK, message);
            }else if(resultCode == RESULT_CODE_NO_NEXT){
                loadDataView.showLoadFailMsg(LoadDataView.REASON_OF_NO_NEXT, message);
            }else if (resultCode == RESULT_CODE_SERVER_ERROR){
                loadDataView.showLoadFailMsg(LoadDataView.REASON_OF_SERVER_ERROR, message);
            }else {
                loadDataView.showLoadFailMsg(LoadDataView.REASON_OF_NO_DATA, message);
            }
        }
        loadDataView.hideProgress();
    }

    @Override
    public void cancel() {
        loadDataModel.cancel();
    }

    public abstract LoadDataModel<T, R> getLoadDataModel();
}
