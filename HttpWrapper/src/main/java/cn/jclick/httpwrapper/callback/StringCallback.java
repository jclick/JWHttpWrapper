package cn.jclick.httpwrapper.callback;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by XuYingjian on 16/1/11.
 */
public class StringCallback extends Callback{
    @Override
    public void onResponse(int statusCode, Map<String, List<String>> headers, Charset charset, InputStream response) {
        super.onResponse(statusCode, headers, charset, response);
        try {
            onSuccess(string(response, charset));
        } catch (IOException e) {
            e.printStackTrace();
            onFailed(e);
        }
    }

    @Override
    public void onError(Exception exception) {
        super.onError(exception);
        onFailed(exception);
    }

    public void onSuccess(String result){

    }

    public void onFailed(Exception exception){

    }
}
