package jone.helper.mvp.model.load;

import java.util.List;
import java.util.Map;

import core.common.tuple.Tuple2;
import core.common.tuple.Tuple3;

/**
 * Created by jone.sun on 2016/1/12.
 */
public interface LoadDataModel<T, R> {
    int requestMethod();
    /***
     * 第一个参数是url
     * 第二个参数是Headers
     * 第二个参数是Params
     * @param pageIndex
     * @return
     */
    Tuple3<String, Map<String, String>, Map<String, String>> getConfig(int pageIndex);

    /***
     * boolean 为是否设置有下一页的标识
     * @param data
     * @return
     */
    Tuple2<List<T>, Boolean> analysisData(R data);
    void loadData(int pageIndex, final Callback<List<T>> callback);
    void cancel();
}
