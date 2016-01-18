package cn.jclick.httpwrapper.callback;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * 请求返回的状态码
     */
    private int statusCode;

    /**
     * 返回数据字节流长度
     */
    private long contentLength;

    /**
     * 请求返回的headers
     */
    private Map<String, List<String>> headers;

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
        return requestSuccess && parseSuccess;
    }

    public boolean isRequestSuccess() {
        return requestSuccess;
    }

    public void setRequestSuccess(boolean requestSuccess) {
        this.requestSuccess = requestSuccess;
    }

    public boolean isParseSuccess() {
        return isRequestSuccess() && parseSuccess;
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "description='" + description + '\'' +
                ", fromCache=" + fromCache +
                ", success=" + isSuccess() +
                ", requestSuccess=" + requestSuccess +
                ", parseSuccess=" + parseSuccess +
                ", requestTime=" + requestTime +
                ", responseTime=" + responseTime +
                ", statusCode=" + statusCode +
                ", headers=" + headers +
                ", data=" + data +
                '}';
    }
}
