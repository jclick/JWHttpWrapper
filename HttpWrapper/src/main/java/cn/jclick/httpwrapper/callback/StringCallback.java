package cn.jclick.httpwrapper.callback;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.jclick.httpwrapper.request.HttpRequestAgent;

/**
 * Created by XuYingjian on 16/1/11.
 */
public abstract class StringCallback extends Callback{


    @Override
    protected void onSuccess(byte[] bytes) {
        ResponseData<String> responseData = wrapResponseData();
        responseData.setData(string(bytes));
        response(responseData);
    }

    private void response(final ResponseData responseData){
        HttpRequestAgent.getInstance().getConfig().mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(responseData);
            }
        });
    }

    @Override
    protected boolean isCacheProcessSuccess(ResponseData<byte[]> data) {
        try{
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onFailed(Exception exception) {
        Log.e(ByteCallback.class.getName(), "request failed..", exception);
        ResponseData<String> responseData = wrapResponseData();
        responseData.setFromCache(false);
        responseData.setRequestSuccess(false);
        responseData.setDescription(exception.getMessage());
        onResponse(responseData);
    }

    protected abstract void onResponse(ResponseData<String> responseData);
}
