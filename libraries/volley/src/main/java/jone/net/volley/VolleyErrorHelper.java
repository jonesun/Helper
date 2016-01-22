package jone.net.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**以下是Volley的异常列表：
 /*AuthFailureError：如果在做一个HTTP的身份验证，可能会发生这个错误。
 /*NetworkError：Socket关闭，服务器宕机，DNS错误都会产生这个错误。
 /*NoConnectionError：和NetworkError类似，这个是客户端没有网络连接。
 /*ParseError：在使用JsonObjectRequest或JsonArrayRequest时，如果接收到的JSON是畸形，会产生异常。
 /*ServerError：服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
 /*TimeoutError：Socket超时，服务器太忙或网络延迟会产生这个异常。默认情况下，Volley的超时时间为2.5秒。如果得到这个错误可以使用RetryPolicy。
 */

public class VolleyErrorHelper {

    private static final String volley_timeout_error = "网络连接超时(TimeoutError)";
    private static final String volley_auth_failure_error = "身份认证失败(AuthFailureError)";
    private static final String volley_network_error = "网络连接异常(NetworkError)";
    private static final String volley_no_connection_error = "无网络(NoConnectionError)";
    private static final String volley_server_error = "服务器异常(ServerError)";
    private static final String volley_unknown_error = "未知异常";
    /**
     * Returns appropriate message which is to be displayed to the user against
     * the specified error object.
     *
     * @param error
     * @return
     */
    public static String getMessage(VolleyError error) {
        if (error instanceof TimeoutError) {
            return volley_timeout_error + ">>" + error.getMessage();
        } else if (isServerProblem(error)) {
            return handleServerError(error);
        } else if(error instanceof NoConnectionError){
            return volley_no_connection_error + ">>" + error.getMessage();
        }else if (error instanceof NetworkError) {
            return volley_network_error + ">>" + error.getMessage();
        }
        return volley_unknown_error + ">>" + error.getMessage();
    }

//    /**
//     * Determines whether the error is related to network
//     *
//     * @param error
//     * @return
//     */
//    private static boolean isNetworkProblem(Object error) {
//        return (error instanceof NetworkError)
//                || (error instanceof NoConnectionError);
//    }

    /**
     * Determines whether the error is related to server
     *
     * @param error
     * @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError)
                || (error instanceof AuthFailureError);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock
     * message or to show a message retrieved from the server.
     *
     * @return
     */
    private static String handleServerError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        HashMap<String, String> result = new Gson().fromJson(
                                new String(response.data),
                                new TypeToken<Map<String, String>>() {
                                }.getType());
                        if (result != null && result.containsKey("error")) {
                            return volley_server_error + ">>" + response.statusCode + ": " + result.get("error");
                        }
                    } catch (Exception e) {
                        Log.e("VolleyErrorHelper", "handleServerError", e);
                    }
                    return volley_server_error + ">>" +  response.statusCode + ": " + error.getMessage();
                default:
                    return volley_server_error + ">>" +  response.statusCode + ": " + error.getMessage();
            }
        }
        return volley_unknown_error + ">>" + error.getMessage();
    }
}