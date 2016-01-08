package cn.jclick.httpwrapper.request;

import java.util.ArrayList;
import java.util.List;

import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;

/**
 * Created by XuYingjian on 16/1/6.
 */
public final class RequestConfig {
    public final String baseUrl;
    public final long connectionTimeOut;
    public final int maxConnections;
    public final long maxRetries;
    public final List<HandlerInterceptor> interceptorList;
    public final boolean urlEncodeEnable;

    private RequestConfig(final Builder builder) {

        this.baseUrl = builder.baseUrl;
        this.connectionTimeOut = builder.connectionTimeOut;
        this.interceptorList = builder.interceptorList;
        this.urlEncodeEnable = builder.urlEncodeEnable;
        this.maxConnections = builder.maxConnections;
        this.maxRetries = builder.maxRetries;
    }

    public static class Builder{

        private String baseUrl;
        private long connectionTimeOut;
        private int maxConnections;
        private long maxRetries;
        private List<HandlerInterceptor> interceptorList = new ArrayList<>();

        private boolean urlEncodeEnable = true;

        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder maxConnections(int maxConnections){
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder maxRetries(long maxRetries){
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder connectionTimeOut(long millSeconds){
            this.connectionTimeOut = millSeconds;
            return this;
        }

        public Builder addInterceptor(HandlerInterceptor interceptor){
            if (!interceptorList.contains(interceptor)){
                interceptorList.add(interceptor);
            }
            return this;
        }
    }
}
