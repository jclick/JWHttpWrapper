package cn.jclick.httpwrapper.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.jclick.httpwrapper.request.HttpRequestAgent;

/**
 * Created by XuYingjian on 16/1/15.
 */
public abstract class ObjectCallback<T> extends Callback{
    private TypeReference<T> typeReference;

    public ObjectCallback(TypeReference<T> typeReference){
        this.typeReference = typeReference;
    }

    @Override
    protected boolean isCacheProcessSuccess(ResponseData<String> data) {
        try{
            if (!super.isCacheProcessSuccess(data)){
                return false;
            }
            ResponseData responseData = convertCache(data);
            T result = processData(data.getData());
            if (result == null){
                return false;
            }
            responseData.setData(result);
            response(responseData);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onSuccess(byte[] bytes) {
        ResponseData<T> responseData = wrapResponseData();
        T result = null;
        try {
            result = processData(string(bytes));
        } catch (Exception e) {
            responseData.setParseSuccess(false);
            responseData.setDescription(e.getMessage());
        }
        responseData.setData(result);
        response(responseData);
    }

    private T processData(String data) throws Exception{
        T result;
        if (this.typeReference != null){
            result = JSON.parseObject(data, this.typeReference);
        }else {
            result = com.alibaba.fastjson.JSONObject.parseObject(data, new TypeReference<T>() {
            });
        }
        return result;
    }

    private void response(final ResponseData<T> responseData){
        HttpRequestAgent.getInstance().getConfig().mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(responseData);
            }
        });
    }

    @Override
    protected void onFailed(Exception exception) {
        response(wrapFailedData(exception));
    }

    protected abstract void onResponse(ResponseData<T> responseData);
}
