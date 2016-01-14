package cn.jclick.httpwrapper.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.callback.ResponseData;
import cn.jclick.httpwrapper.request.RequestParams;

/**
 * Created by jclick on 16/1/6.
 */
public interface HandlerInterceptor {

    /**
     * 发起请求之前的回调
     * @param params
     * @return 是否继续执行请求
     */
    boolean preHandler(RequestParams params);

    /**
     *请求成功后的回调
     * @param params  请求参数
     * @param statusCode  请求结束的状态码
     * @param headers 请求的headers
     */
    void postSuccessHandler(RequestParams params, int statusCode, Map<String, List<String>> headers);

    /**
     * 请求失败
     * @param exception
     */
    void postFailedHandler(IOException exception);

    /**
     * 执行完毕Callback后的回调
     */
    void afterCompletion(RequestParams params, ResponseData<String> responseData);
}
