package cn.jclick.httpwrapper.callback;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by XuYingjian on 16/1/11.
 */
public class ByteCallback extends Callback{

    @Override
    public void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response) {
        super.onResponse(statusCode, headers, charset, response);
    }

    public void onSuccess(byte[] bytes){

    }

}
