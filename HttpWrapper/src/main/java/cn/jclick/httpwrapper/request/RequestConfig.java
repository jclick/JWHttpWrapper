package cn.jclick.httpwrapper.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.callback.CallBack;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;

/**
 * Created by XuYingjian on 16/1/6.
 */
public final class RequestConfig {
    final String url;
    final String baseUrl;
    final Object tag;
    final long connectionTimeOut;
    final RequestMethod requestMethod;
    final Map<String, Object> requestParams;
    final Map<String, String> requestHeaders;
    final List<HandlerInterceptor> interceptorList;
    final CallBack callBack;

    final boolean urlEncodeEnable;

    private RequestConfig(final Builder builder) {

        this.url = builder.url;
        this.baseUrl = builder.baseUrl;
        this.tag = builder.tag;
        this.connectionTimeOut = builder.connectionTimeOut;
        this.requestMethod = builder.requestMethod;
        this.requestParams = builder.requestParams;
        this.requestHeaders = builder.requestHeaders;
        this.interceptorList = builder.interceptorList;
        this.callBack = builder.callBack;
        this.urlEncodeEnable = builder.urlEncodeEnable;
    }

    public static class Builder{

        private String url;
        private String baseUrl;
        private Object tag;
        private long connectionTimeOut;
        private RequestMethod requestMethod;
        private Map<String, Object> requestParams;
        private Map<String, String> requestHeaders;
        private List<HandlerInterceptor> interceptorList = new ArrayList<>();

        private boolean urlEncodeEnable = true;

        private CallBack callBack;

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

        public Builder urlEncodeEnable(boolean enable){
            this.urlEncodeEnable = enable;
            return this;
        }

        public Builder connectionTimeOut(long millSeconds){
            this.connectionTimeOut = millSeconds;
            return this;
        }

        public Builder callback(CallBack callBack){
            this.callBack = callBack;
            return this;
        }

        public Builder addInterceptor(HandlerInterceptor interceptor){
            if (!interceptorList.contains(interceptor)){
                interceptorList.add(interceptor);
            }
            return this;
        }

        public Builder requestParams(Map<String, Object> requestParams){
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

        public Builder head(){
            this.requestMethod = RequestMethod.RequestMethodHead;
            return this;
        }

        public Builder patch(){
            this.requestMethod = RequestMethod.RequestMethodPatch;
            return this;
        }
    }

    public enum RequestMethod{
        RequestMethodGet, RequestMethodPost, RequestMethodPut, RequestMethodDelete, RequestMethodHead, RequestMethodPatch
    }
}
