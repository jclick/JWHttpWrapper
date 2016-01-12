package cn.jclick.httpwrapper.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by XuYingjian on 16/1/11.
 */
public abstract class ByteCallback extends Callback{

    @Override
    public void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response) {
        super.onResponse(statusCode, headers, charset, response);
        try {
            onSuccess(false, bytes(response));
        } catch (IOException e) {
            e.printStackTrace();
            onFailed(e);
        }
    }

    protected void onSuccess(boolean fromCache, byte[] bytes) {
    }

    public void onSuccess(byte[] bytes){

    }

}
