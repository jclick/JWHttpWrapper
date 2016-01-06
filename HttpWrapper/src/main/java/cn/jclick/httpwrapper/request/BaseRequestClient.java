package cn.jclick.httpwrapper.request;

/**
 * Created by XuYingjian on 16/1/6.
 */
public abstract class BaseRequestClient {

    protected RequestConfig config;

    public abstract BaseRequestClient BaseRequestClient(RequestConfig config);

    public abstract void start();

    public abstract void stop();

    public String getAbsoluteUrl(){
        return null;
    }

}
