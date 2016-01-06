package cn.jclick.httpwrapper.interceptor;

/**
 * Created by jclick on 16/1/6.
 */
public interface HandlerInterceptor {

    boolean preHandler();

    void postHandle();

    void afterCompletion();
}
