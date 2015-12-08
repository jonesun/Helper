package jone.helper.callbacks;

/**
 * Created by jone.sun on 2015/12/2.
 */
public interface CallbackListener<T> {
    /**
     * 请求的响应结果为成功时调用
     * @param data  返回的数据
     */
    public void onSuccess(T data);

    /**
     * 请求的响应结果为失败时调用
     * @param errorEvent 错误码
     * @param message    错误信息
     */
    public void onFailure(String errorEvent, String message);
}
