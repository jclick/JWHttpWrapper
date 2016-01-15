package cn.jclick.httpwrapper.callback;

import android.util.Log;

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
    protected boolean isCacheProcessSuccess(ResponseData<String> data) {
        try{
            if (!super.isCacheProcessSuccess(data)){
                return false;
            }
            ResponseData responseData = convertCache(data);
            responseData.setData(data.getData());
            response(responseData);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onFailed(Exception exception) {
        response(wrapFailedData(exception));
    }

    protected abstract void onResponse(ResponseData<String> responseData);
}
