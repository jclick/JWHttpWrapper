package cn.jclick.httpwrapper.request;

import android.text.TextUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.jclick.httpwrapper.callback.ResponseData;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import cn.jclick.httpwrapper.utils.WrapperUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static okhttp3.internal.Util.UTF_8;

/**
 * Created by XuYingjian on 16/1/6.
 */
public class HttpRequestAgent {

    private static HttpRequestAgent INSTANCE;

    private OkHttpClient okHttpClient;

    private RequestConfig requestConfig;
    private final Map<Object, List<Call>> allRequestMap = Collections
            .synchronizedMap(new HashMap<Object, List<Call>>());

    private ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "HttpRequestAgent" + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    });

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

    public void init(RequestConfig config){
        if (config == null){
            throw new NullPointerException("RequestConfig Can not Null");
        }
        this.requestConfig = config;
        okHttpClient = new OkHttpClient().newBuilder().connectTimeout(config.connectionTimeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(new RetryInterceptor(config.maxRetries))
                .connectionPool(new ConnectionPool(config.maxConnections, 5, TimeUnit.SECONDS))
                .build();
    }

    public void executeRequest(RequestParams params, final cn.jclick.httpwrapper.callback.Callback callback){
        executorService.execute(new RequestThread(params, callback));
    }

    private Request buildRequest(RequestParams params, Request.Builder builder, String url){
        Request request = null;
        if (params.requestMethod == RequestParams.RequestMethod.RequestMethodGet){
            url = WrapperUtils.getUrlWithQueryString(params.urlEncodeEnable == null ? getConfig().urlEncodeEnable : params.urlEncodeEnable, url, params);
            request = builder.url(url).get().build();
        }else{
            builder = builder.url(url);
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
                removeByTag(obj);
            }
        }
    }

    public void interruptAllRequest(){
        if (okHttpClient != null){
            okHttpClient.dispatcher().cancelAll();
            allRequestMap.clear();
        }
    }

    public RequestConfig getConfig() {
        return requestConfig;
    }

    private synchronized void addCall(Call call, Object tag){

        if (tag == null){
            return;
        }
        if (call == null || call.isCanceled()){
            return;
        }
        List<Call> requestList;
        // Add request to request map
        synchronized (allRequestMap) {
            requestList = allRequestMap.get(tag);
            if (requestList == null) {
                requestList = Collections.synchronizedList(new LinkedList<Call>());
                allRequestMap.put(tag, requestList);
            }
        }

        requestList.add(call);

        Iterator<Call> iterator = requestList.iterator();
        while (iterator.hasNext()) {
            Call targetCall = iterator.next();
            if (targetCall.isCanceled()) {
                iterator.remove();
            }
        }
    }

    private synchronized void removeByTag(Object tag){
        if (tag == null){
            return;
        }
        synchronized (allRequestMap){
            List<Call> list = allRequestMap.get(tag);
            if (list == null || list.isEmpty()){
                return;
            }
            Iterator<Call> iterator = list.iterator();
            while (iterator.hasNext()) {
                Call targetCall = iterator.next();
                targetCall.cancel();
                iterator.remove();
            }
        }
    }

    private synchronized void removeCallByTag(Object tag, Call call){
        if (tag == null){
            if (call != null){
                removeCall(call);
            }
            return;
        }
        if (call == null){
            removeByTag(tag);
            return;
        }
        call.cancel();
        synchronized (allRequestMap){
            List<Call> list = allRequestMap.get(tag);
            if (list == null || list.isEmpty()){
                return;
            }
            list.remove(call);
        }
    }

    private synchronized void removeCall(Call call){
        if (call == null){
            return;
        }
        call.cancel();
        synchronized (allRequestMap){
            for (Object tag : allRequestMap.keySet()){
                List<Call> requestList = allRequestMap.get(tag);
                for (Call c : requestList){
                    if (c.isCanceled()){
                        requestList.remove(c);
                    }else if (c == call){
                        requestList.remove(c);
                    }
                }
            }
        }
    }

    private final class RetryInterceptor implements Interceptor {

        private int maxRetry;

        public RetryInterceptor(int maxRetry){
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            // try the request
            Response response = chain.proceed(request);

            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < maxRetry) {
                tryCount++;
                response = chain.proceed(request);
            }
            return response;
        }
    }

    private class RequestThread implements Runnable{
        private RequestParams params;
        private WeakReference<cn.jclick.httpwrapper.callback.Callback> weakCallback;

        public RequestThread(RequestParams params, cn.jclick.httpwrapper.callback.Callback callback){
            this.params = params;
            weakCallback = new WeakReference<cn.jclick.httpwrapper.callback.Callback>(callback);
        }

        @Override
        public void run() {
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
            String baseUrl = null, url;
            if (!TextUtils.isEmpty(params.baseUrl)){
                baseUrl = params.baseUrl;
            }else{
                if (requestConfig != null){
                    baseUrl = requestConfig.baseUrl;
                }
            }
            url = params.url;
            if (!TextUtils.isEmpty(baseUrl)){
                url = baseUrl.concat(url);
            }

            List<HandlerInterceptor> interceptorList = requestConfig.interceptorList;
            if (!preHandler(interceptorList)){
                return;
            }

            if (weakCallback.get() != null){
                boolean isNeedRequest = weakCallback.get().beforeStart(params);
                if (!isNeedRequest){
                    return;
                }
            }
            final Request request = buildRequest(params, builder, url);
            final Call call = okHttpClient.newCall(request);
            addCall(call, tag);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    removeCallByTag(tag, call);
                    if (weakCallback.get() != null){
                        weakCallback.get().onError(e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    for (HandlerInterceptor interceptor : requestConfig.interceptorList){
                        interceptor.postSuccessHandler(params, response.code(), response.headers().toMultimap());
                    }
                    if(weakCallback.get() != null){
                        MediaType mediaType = response.body().contentType();
                        Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
                        ResponseData<String> data = weakCallback.get().onResponse(response.code(), response.headers().toMultimap(), charset, response.body().byteStream(), response.body().contentLength());
                        for (HandlerInterceptor interceptor : requestConfig.interceptorList){
                            interceptor.afterCompletion(params, data);
                        }
                    }
                    removeCallByTag(tag, call);
                }
            });
        }

        private boolean preHandler(List<HandlerInterceptor> list){

            boolean isSuccess = true;
            for (HandlerInterceptor interceptor : list){
                if (!isSuccess){
                    isSuccess = interceptor.preHandler(params);
                }else{
                    interceptor.preHandler(params);
                }
            }
            return isSuccess;
        }
    }
}
