package cn.jclick.httpwrapper.request;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by XuYingjian on 16/1/6.
 */
public class HttpRequestAgent {

    private static HttpRequestAgent INSTANCE;

    private OkHttpClient okHttpClient;
    private String baseUrl;

    private RequestConfig config;
    private List<HandlerInterceptor> interceptorList;
    private final Map<Object, Call> allRequestMap = Collections
            .synchronizedMap(new HashMap<Object, Call>());

    private HttpRequestAgent(){

    }

    public static HttpRequestAgent getInstance(){
        if (INSTANCE == null) {
            synchronized (HttpRequestAgent.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpRequestAgent();
                }
            }
        }
        return INSTANCE;
    }

    public void init(final RequestConfig config){
        this.config = config;
        if (config == null){
            okHttpClient = new OkHttpClient().newBuilder().build();
        }else{
            interceptorList = config.interceptorList;
            this.baseUrl = config.baseUrl;
            okHttpClient = new OkHttpClient().newBuilder().connectTimeout(config.connectionTimeOut, TimeUnit.MILLISECONDS)
                    .connectionPool(new ConnectionPool(config.maxConnections, 5, TimeUnit.SECONDS))
                    .build();
            okHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    // try the request
                    Response response = chain.proceed(request);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < config.maxRetries) {
                        tryCount++;
                        response = chain.proceed(request);
                    }
                    return response;
                }
            });
        }
    }

    public void executeRequest(RequestParams params){
        String baseUrl, url;
        if (!TextUtils.isEmpty(params.baseUrl)){
            baseUrl = params.baseUrl;
        }else{
            baseUrl = this.baseUrl;
        }
        url = params.url;
        if (params.urlEncodeEnable && params.requestMethod == RequestParams.RequestMethod.RequestMethodGet){

        }else{

        }
        Request request = new Request.Builder().tag(this).build();
    }

    public synchronized void interruptRequestByTag(Object ...tags){

        for (Object obj : tags){
            if (allRequestMap.containsKey(obj)){
                allRequestMap.get(obj).cancel();
                allRequestMap.remove(obj);
            }
        }
    }

    public void interruptAllRequest(){
        if (okHttpClient != null){
            okHttpClient.dispatcher().cancelAll();
        }
    }
}
