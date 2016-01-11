package cn.jclick.httpwrapper.interceptor;

/**
 * Created by XuYingjian on 16/1/11.
 */
public class LoggerInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandler() {
        return false;
    }

    @Override
    public void postHandle() {

    }

    @Override
    public void afterCompletion() {

    }
}
