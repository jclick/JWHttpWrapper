package cn.jclick.httpwrapper.callback;

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
                return isCacheProcessSuccess(cache());
            case CACHE_WHEN_NO_NETWORK:
                if (WrapperUtils.isOnline(HttpRequestAgent.getInstance().getConfig().context)) {
                    return isCacheProcessSuccess(cache());
                }
                break;
        }

        return true;
    }

    public byte[] bytes(InputStream stream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(stream);
        IOUtils.closeQuietly(stream);
        return bytes;
    }

    public String string(InputStream stream, Charset charset) throws IOException {
        return new String(bytes(stream), charset);
    }

    public String cache() {
        return HttpRequestAgent.getInstance().getCache(cacheURL);
    }

    protected boolean isCacheProcessSuccess(String data) {
        return true;
    }

    public final void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.charset = charset;
        this.responseDate = new Date();

        try {
            byte[] bytes = bytes(response);
            if (params.cacheMode != RequestConfig.HttpCacheMode.NO_CACHE) {
                if (HttpRequestAgent.getInstance().getConfig().diskCache != null) {
                    boolean flag = HttpRequestAgent.getInstance().getConfig().diskCache.putBytes(cacheURL, bytes);
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

    ;

    public void onError(Exception exception) {
        if (params.cacheMode == RequestConfig.HttpCacheMode.FAILED_SHOW_CACHE) {
            String data = cache();
            if (isCacheProcessSuccess(data)) {
                return;
            }
        } else {
            onFailed(exception);
        }
    }

    protected abstract void onSuccess(byte[] bytes);

    protected abstract void onFailed(Exception exception);
}
