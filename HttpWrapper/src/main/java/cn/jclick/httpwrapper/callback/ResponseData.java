package cn.jclick.httpwrapper.callback;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by XuYingjian on 16/1/12.
 */
public class ResponseData<T> implements Serializable{

    private T data;
    /**
     * 如果失败的话，描述失败原因
     */
    private String description;
    /**
     * 是否为缓存数据
     */
    private boolean fromCache;
    /**
     * 是否从server请求数据成功并且解析数据成功
     */
    private boolean success;
    /**
     * 是否请求成功
     */
    private boolean requestSuccess;
    /**
     * 是否解析成功。
     */
    private boolean parseSuccess;
    /**
     * 请求的时间，若为缓存数据，则为缓存的请求时间
     */
    private Date requestTime;
    /**
     * 请求结束的时间，若为缓存数据，则为缓存的请求结束时间
     */
    private Date responseTime;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isRequestSuccess() {
        return requestSuccess;
    }

    public void setRequestSuccess(boolean requestSuccess) {
        this.requestSuccess = requestSuccess;
    }

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }
}
