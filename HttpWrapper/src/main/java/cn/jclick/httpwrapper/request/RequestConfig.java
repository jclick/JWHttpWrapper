package cn.jclick.httpwrapper.request;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jclick.httpwrapper.cache.IDiskCache;
import cn.jclick.httpwrapper.cache.LruDiskCache;
import cn.jclick.httpwrapper.cache.Md5FileNameGenerator;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import cn.jclick.httpwrapper.interceptor.LoggerInterceptor;
import cn.jclick.httpwrapper.utils.StorageUtils;

/**
 * Created by XuYingjian on 16/1/6.
 */
public final class RequestConfig {

    private static final LoggerInterceptor loggerInterceptor = new LoggerInterceptor();
    private static final long DEFAULT_CACHE_SECONDS = 10 * 60;
    private static final HttpCacheMode DEFAULT_CACHE_MODE = HttpCacheMode.NO_CACHE;

    public final String baseUrl;
    public final long connectionTimeOut;
    public final int maxConnections;
    public final int maxRetries;

    public IDiskCache diskCache;
    public final List<HandlerInterceptor> interceptorList;
    public final boolean urlEncodeEnable;
    public final long cacheTimeInSeconds;
    public final HttpCacheMode cacheMode;
    public final Context context;
    public final Handler mainHandler;
    public final boolean logEnable;

    private RequestConfig(final Builder builder) {

        this.baseUrl = builder.baseUrl;
        this.connectionTimeOut = builder.connectionTimeOut;
        this.interceptorList = builder.interceptorList;
        this.urlEncodeEnable = builder.urlEncodeEnable;
        this.maxConnections = builder.maxConnections;
        this.maxRetries = builder.maxRetries;
        if (builder.cacheTimeInSeconds < 0){
            this.cacheTimeInSeconds = DEFAULT_CACHE_SECONDS;
        }else{
            this.cacheTimeInSeconds = builder.cacheTimeInSeconds;
        }
        if(builder.cacheMode == null){
            this.cacheMode = DEFAULT_CACHE_MODE;
        }else{
            this.cacheMode = builder.cacheMode;
        }
        this.context = builder.context;
        this.mainHandler = new Handler(context.getMainLooper());
        if(builder.diskCache == null){
            File cacheFileDir = StorageUtils.getCacheDirectory(this.context);
            try {
                this.diskCache = new LruDiskCache(cacheFileDir, new Md5FileNameGenerator(), 100 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            this.diskCache = builder.diskCache;
        }
        this.logEnable = builder.logEnable;
    }

    public static class Builder{

        private String baseUrl;
        private long connectionTimeOut;
        private int maxConnections;
        private int maxRetries;
        private long cacheTimeInSeconds;
        private HttpCacheMode cacheMode;
        private List<HandlerInterceptor> interceptorList = new ArrayList<>();
        private IDiskCache diskCache;
        private boolean urlEncodeEnable = true;
        private Context context;

        private boolean logEnable;

        public Builder(Context context){
            this.context = context.getApplicationContext();
        }

        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder maxConnections(int maxConnections){
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder maxRetries(int maxRetries){
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

        public Builder diskCache(IDiskCache diskCache){
            this.diskCache = diskCache;
            return this;
        }

        public Builder cacheTimeInSeconds(long cacheTimeInSeconds){
            this.cacheTimeInSeconds = cacheTimeInSeconds;
            return this;
        }

        public Builder logEnable(boolean logEnable){
            this.logEnable = logEnable;
            if (this.logEnable){
                interceptorList.add(loggerInterceptor);
            }else{
                interceptorList.remove(loggerInterceptor);
            }
            return this;
        }

        public Builder cacheMode(HttpCacheMode cacheMode){
            this.cacheMode = cacheMode;
            return this;
        }

        public RequestConfig build(){
            return new RequestConfig(this);
        }

    }

    public enum HttpCacheMode{
        NO_CACHE,//不使用缓存
        ALWAYS_CACHE,//缓存时间内，不发请求，直接返回缓存结果
        CACHE_FIRST,//优先返回缓存结果，然后发送请求.（总共返回二次数据）
        FAILED_SHOW_CACHE,//请求失败后展示缓存
        CACHE_WHEN_NO_NETWORK//没有网络的时候展示缓存
    }
}
