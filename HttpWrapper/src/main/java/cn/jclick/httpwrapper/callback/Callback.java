package cn.jclick.httpwrapper.callback;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.interceptor.LoggerInterceptor;
import cn.jclick.httpwrapper.request.HttpRequestAgent;
import cn.jclick.httpwrapper.request.RequestConfig;
import cn.jclick.httpwrapper.request.RequestParams;
import cn.jclick.httpwrapper.utils.IOUtils;
import cn.jclick.httpwrapper.utils.WrapperUtils;

/**
 * Created by jclick on 16/1/6.
 */
public abstract class Callback {
    protected Date startRequestDate;
    protected Date responseDate;
    protected long contentLength;
    protected RequestParams params;
    protected int statusCode;
    protected Map<String, List<String>> headers;
    protected Charset charset;

    protected String cacheURL;

    public boolean beforeStart(RequestParams params) {
        this.startRequestDate = new Date();
        this.params = params;
        this.cacheURL = WrapperUtils.getUrlWithQueryString(params);
        boolean isContinue = true;
        switch (params.cacheMode) {
            case ALWAYS_CACHE:
                 isContinue = !isCacheProcessSuccess(cache());
                break;
            case CACHE_WHEN_NO_NETWORK:
                if (!WrapperUtils.isOnline(HttpRequestAgent.getInstance().getConfig().context)) {
                    isContinue = !isCacheProcessSuccess(cache());
                }
                break;
            case CACHE_FIRST:
                isCacheProcessSuccess(cache());
                break;
        }
        if (!isContinue && HttpRequestAgent.getInstance().getConfig().logEnable){
            Log.i(LoggerInterceptor.class.getName(), "Response from cache and stop the request!\n" + cache());
        }
        return isContinue;
    }

    public final byte[] bytes(InputStream stream) throws IOException {
        byte[] bytes;
        try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            long count = 0;
            int n = 0;
            byte[] arr = new byte[4196];
            while (-1 != (n = stream.read(arr))) {
                output.write(arr, 0, n);
                count += n;

                sendProgress(count, contentLength);
            }
            if (count > Integer.MAX_VALUE) {
                throw new IOException("input stream length error");
            }
            bytes = output.toByteArray();

        }finally {
            IOUtils.closeQuietly(stream);
        }
        return bytes;
    }

    public final void sendProgress(final long writeLength, final long contentLength){
        HttpRequestAgent.getInstance().getConfig().mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onProgress(writeLength, contentLength);
            }
        });
    }

    public final String string(InputStream stream, Charset charset) throws IOException {
        return new String(bytes(stream), charset);
    }

    public final String string(byte[] bytes){
        return new String(bytes, charset);
    }

    public ResponseData<String> cache() {
        if (HttpRequestAgent.getInstance().getConfig().diskCache != null){
            return HttpRequestAgent.getInstance().getConfig().diskCache.getData(cacheURL);
        }
        return null;
    }

    protected boolean isCacheProcessSuccess(ResponseData<String> data) {
        if (data == null){
            return false;
        }
        if (new Date().getTime() - data.getRequestTime().getTime() > params.cacheTimeInSeconds * 1000){
            return false;
        }
        return true;
    }

    /**
     *
     * @param cacheData cache数据
     * @return 转换为要返回的数据
     */
    protected final ResponseData convertCache(ResponseData<String> cacheData){
        ResponseData<String> responseData = new ResponseData<>();
        responseData.setFromCache(true);
        responseData.setDescription(cacheData.getDescription());
        responseData.setRequestTime(cacheData.getRequestTime());
        responseData.setRequestSuccess(cacheData.isRequestSuccess());
        responseData.setParseSuccess(cacheData.isParseSuccess());
        responseData.setStatusCode(cacheData.getStatusCode());
        responseData.setHeaders(cacheData.getHeaders());
        responseData.setResponseTime(cacheData.getResponseTime());
        return responseData;
    }

    public final ResponseData<String> onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response, long contentLength) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.charset = charset;
        this.contentLength = contentLength;
        this.responseDate = new Date();
        return onSuccess(response);
    }

    protected ResponseData<String> onSuccess(InputStream inputStream){
        ResponseData<String> responseData = wrapResponseData();
        try {
            byte[] bytes = bytes(inputStream);
            if (params.cacheMode != RequestConfig.HttpCacheMode.NO_CACHE) {
                if (HttpRequestAgent.getInstance().getConfig().diskCache != null) {
                    responseData.setData(string(bytes));
                    responseData.setFromCache(true);
                    boolean flag = HttpRequestAgent.getInstance().getConfig().diskCache.putData(cacheURL, responseData);
                    if (!flag) {
                        Log.d(getClass().getName(), "response success, but save cache failed !");
                    }
                }
            }
            onSuccess(bytes);
        } catch (IOException e) {
            responseData.setParseSuccess(false);
            onError(e);
        }
        return responseData;
    }

    public final void onError(Exception exception) {
        responseDate = new Date();
        if (params.cacheMode == RequestConfig.HttpCacheMode.FAILED_SHOW_CACHE) {
            if (isCacheProcessSuccess(cache())) {
                return;
            }
        } else {
            onFailed(exception);
        }
    }

    protected ResponseData wrapResponseData(){
        ResponseData responseData = new ResponseData();
        responseData.setParseSuccess(true);
        responseData.setRequestSuccess(true);
        responseData.setFromCache(false);
        responseData.setContentLength(contentLength);
        responseData.setRequestTime(startRequestDate);
        responseData.setResponseTime(responseDate);
        responseData.setStatusCode(statusCode);
        responseData.setHeaders(headers);
        return responseData;
    }

    public ResponseData wrapFailedData(Exception exception){
        Log.e(ByteCallback.class.getName(), "request failed..", exception);
        ResponseData responseData = wrapResponseData();
        responseData.setFromCache(false);
        responseData.setRequestSuccess(false);
        responseData.setDescription(exception.getMessage());
        return responseData;
    }

    public void onProgress(long bytesWritten, long totalSize) {
        if (HttpRequestAgent.getInstance().getConfig().logEnable){
            Log.i("onProgress", String.format("Progress %d from %d (%2.0f%%)", bytesWritten, totalSize, (totalSize > 0) ? (bytesWritten * 1.0 / totalSize) * 100 : -1));
        }
    }

    protected void onFailed(Exception exception) {

    }

    protected abstract void onSuccess(byte[] bytes);
}
