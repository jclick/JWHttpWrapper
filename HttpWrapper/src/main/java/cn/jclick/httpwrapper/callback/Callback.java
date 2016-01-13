package cn.jclick.httpwrapper.callback;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    protected RequestParams params;
    protected int statusCode;
    protected Map<String, List<String>> headers;
    protected Charset charset;

    protected String cacheURL;

    public boolean beforeStart(RequestParams params) {
        this.startRequestDate = new Date();
        this.params = params;
        this.cacheURL = WrapperUtils.getUrlWithQueryString(params);
        switch (params.cacheMode) {
            case ALWAYS_CACHE:
                return !isCacheProcessSuccess(cache());
            case CACHE_WHEN_NO_NETWORK:
                if (WrapperUtils.isOnline(HttpRequestAgent.getInstance().getConfig().context)) {
                    return !isCacheProcessSuccess(cache());
                }
                break;
            case CACHE_FIRST:
                isCacheProcessSuccess(cache());
                break;
        }

        return true;
    }

    public final byte[] bytes(InputStream stream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(stream);
        IOUtils.closeQuietly(stream);
        return bytes;
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
        responseData.setResponseTime(cacheData.getResponseTime());
        return responseData;
    }

    public final void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.charset = charset;
        this.responseDate = new Date();
        onSuccess(response);
    }

    protected void onSuccess(InputStream inputStream){
        try {
            byte[] bytes = bytes(inputStream);
            if (params.cacheMode != RequestConfig.HttpCacheMode.NO_CACHE) {
                if (HttpRequestAgent.getInstance().getConfig().diskCache != null) {
                    ResponseData<String> responseData = wrapResponseData();
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
            onError(e);
        }
    }

    public void onError(Exception exception) {
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
        responseData.setRequestTime(startRequestDate);
        responseData.setResponseTime(responseDate);
        return responseData;
    }

    protected abstract void onSuccess(byte[] bytes);

    protected abstract void onFailed(Exception exception);
}
