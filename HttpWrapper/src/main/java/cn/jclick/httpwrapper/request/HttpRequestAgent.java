package cn.jclick.httpwrapper.request;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import cn.jclick.httpwrapper.utils.UrlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        if (!TextUtils.isEmpty(baseUrl)){
            url = baseUrl.concat(url);
        }
        Request.Builder builder = new Request.Builder();
        if (params.tag != null){
            builder = builder.tag(params.tag);
        }
        if (params.requestHeaders != null){
            for (String key : params.requestHeaders.keySet()){
                builder = builder.addHeader(key, params.requestHeaders.get(key));
            }
        }
        final Object tag = params.tag;
        Request request = buildRequest(params, builder, url);
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (tag != null){
                    allRequestMap.remove(tag);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (tag != null){
                    allRequestMap.remove(tag);
                }
            }
        });
    }

    private Request buildRequest(RequestParams params, Request.Builder builder, String url){
        Request request = null;
        if (params.requestMethod == RequestParams.RequestMethod.RequestMethodGet){
            url = UrlUtils.getUrlWithQueryString(params.urlEncodeEnable, url, params);
            request = builder.url(url).get().build();
        }else{
            RequestBody requestBody = RequestBuilder.buildRequestBody(params);
            switch (params.requestMethod){
                case RequestMethodDelete:
                    builder = builder.delete(requestBody);
                    break;
                case RequestMethodHead:
                    builder = builder.head();
                    break;
                case RequestMethodPatch:
                    builder = builder.patch(requestBody);
                    break;
                case RequestMethodPost:
                    builder = builder.post(requestBody);
                    break;
                case RequestMethodPut:
                    builder = builder.put(requestBody);
                    break;
            }
            request = builder.build();
        }
        return request;
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
