package cn.jclick.httpwrapper.interceptor;

/**
 * Created by jclick on 16/1/6.
 */
public interface HandlerInterceptor {

    boolean preHandler();

    /**
     *
     */
    void postHandle();

    /**
     * 执行完毕Callback后的回调
     */
    void afterCompletion();
}
