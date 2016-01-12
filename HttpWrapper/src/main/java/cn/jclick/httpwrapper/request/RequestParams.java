package cn.jclick.httpwrapper.request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.callback.Callback;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import okhttp3.MediaType;

/**
 * Created by XuYingjian on 16/1/6.
 */
public final class RequestParams {

    private static final String DEFAULT_MEDIA_TYPE = "multipart/form-data";

    public final String url;
    public final String baseUrl;
    public final Object tag;
    public final long connectionTimeOut;
    public final RequestMethod requestMethod;
    public final Map<String, String> requestParams;
    public final Map<String, String> requestHeaders;
    public final File[] uploadFiles;
    public final List<HandlerInterceptor> interceptorList;
    public final Callback callBack;
    public final RequestConfig.HttpCacheMode cacheMode;
    public final long cacheTimeInSeconds;

    public final String mediaType;

    public final Boolean urlEncodeEnable;

    private RequestParams(final Builder builder) {

        this.url = builder.url;
        this.baseUrl = builder.baseUrl;
        this.tag = builder.tag;
        this.uploadFiles = builder.uploadFiles;
        this.connectionTimeOut = builder.connectionTimeOut;
        this.requestMethod = builder.requestMethod;
        this.requestParams = builder.requestParams;
        this.requestHeaders = builder.requestHeaders;
        this.interceptorList = builder.interceptorList;
        this.callBack = builder.callBack;
        this.urlEncodeEnable = builder.urlEncodeEnable;
        if (builder.mediaType == null){
            this.mediaType = DEFAULT_MEDIA_TYPE;
        }else{
            this.mediaType = builder.mediaType;
        }
        if (builder.cacheMode == null){
            cacheMode = HttpRequestAgent.getInstance().getConfig().cacheMode;
        }else{
            cacheMode = builder.cacheMode;
        }

        if (builder.cacheTimeInSeconds <= 0){
            this.cacheTimeInSeconds = HttpRequestAgent.getInstance().getConfig().cacheTimeInSeconds;
        }else{
            this.cacheTimeInSeconds = builder.cacheTimeInSeconds;
        }
    }

    public static class Builder{

        private String url;
        private String baseUrl;
        private Object tag;
        private long connectionTimeOut;
        private RequestMethod requestMethod = RequestMethod.RequestMethodPost;
        private Map<String, String> requestParams;
        private Map<String, String> requestHeaders;
        private File[] uploadFiles;
        private List<HandlerInterceptor> interceptorList = new ArrayList<>();
        private RequestConfig.HttpCacheMode cacheMode;

        private String mediaType;
        private Boolean urlEncodeEnable;

        private Callback callBack;
        private long cacheTimeInSeconds;

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder tag(Object tag){
            this.tag = tag;
            return this;
        }

        public Builder mediaType(String mediaType){
            this.mediaType = mediaType;
            return this;
        }

        public Builder uploadFiles(File ...files){
            this.uploadFiles = files;
            return this;
        }

        public Builder urlEncodeEnable(boolean enable){
            this.urlEncodeEnable = enable;
            return this;
        }

        public Builder connectionTimeOut(long millSeconds){
            this.connectionTimeOut = millSeconds;
            return this;
        }

        public Builder callback(Callback callBack){
            this.callBack = callBack;
            return this;
        }

        public Builder addInterceptor(HandlerInterceptor interceptor){
            if (!interceptorList.contains(interceptor)){
                interceptorList.add(interceptor);
            }
            return this;
        }

        public Builder requestParams(Map<String, String> requestParams){
            this.requestParams = requestParams;
            return this;
        }

        public Builder requestHeaders(Map<String, String> requestHeaders){
            this.requestHeaders = requestHeaders;
            return this;
        }

        public Builder get(){
            this.requestMethod = RequestMethod.RequestMethodGet;
            return this;
        }

        public Builder post(){
            this.requestMethod = RequestMethod.RequestMethodPost;
            return this;
        }

        public Builder put(){
            this.requestMethod = RequestMethod.RequestMethodPut;
            return this;
        }

        public Builder delete(){
            this.requestMethod = RequestMethod.RequestMethodDelete;
            return this;
        }

        public Builder cacheMode(RequestConfig.HttpCacheMode cacheMode){
            this.cacheMode = cacheMode;
            return this;
        }

        public Builder cacheTimeInSeconds(long cacheTimeInSeconds){
            this.cacheTimeInSeconds = cacheTimeInSeconds;
            return this;
        }

        public Builder head(){
            this.requestMethod = RequestMethod.RequestMethodHead;
            return this;
        }

        public Builder patch(){
            this.requestMethod = RequestMethod.RequestMethodPatch;
            return this;
        }

        public RequestParams build(){
            return new RequestParams(this);
        }
    }

    public enum RequestMethod{
        RequestMethodGet, RequestMethodPost, RequestMethodPut, RequestMethodDelete, RequestMethodHead, RequestMethodPatch
    }
}
