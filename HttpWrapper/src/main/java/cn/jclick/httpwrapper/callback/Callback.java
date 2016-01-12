package cn.jclick.httpwrapper.callback;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.request.RequestParams;
import cn.jclick.httpwrapper.utils.IOUtils;
import cn.jclick.httpwrapper.utils.WrapperUtils;

/**
 * Created by jclick on 16/1/6.
 */
public abstract class Callback {
    protected RequestParams params;
    protected int statusCode;
    protected Map<String, List<String>> headers;
    protected Charset charset;

    protected String cacheURL;

    public boolean beforeStart(RequestParams params){
        this.params = params;
        this.cacheURL = WrapperUtils.getUrlWithQueryString(params);
        switch (params.cacheMode){
            case ALWAYS_CACHE:

                break;
            case CACHE_WHEN_NO_NETWORK:

                break;
        }

        return true;
    }

    public byte[] bytes(InputStream stream) throws IOException{
        byte[] bytes = IOUtils.toByteArray(stream);
        IOUtils.closeQuietly(stream);
        return bytes;
    }

    public String string(InputStream stream, Charset charset) throws IOException {
        return new String(bytes(stream), charset);
    }

    public void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response){
        this.statusCode = statusCode;
        this.headers = headers;
        this.charset = charset;
    };

    public void onError(Exception exception){

    }
}
