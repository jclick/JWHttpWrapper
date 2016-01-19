package cn.jclick.httpwrapper.callback;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jclick.httpwrapper.request.HttpRequestAgent;
import cn.jclick.httpwrapper.request.RequestConfig;
import cn.jclick.httpwrapper.utils.IOUtils;

/**
 * Created by XuYingjian on 16/1/19.
 */
public abstract class FileCallback extends Callback{

    private String absoluteFilePath;

    public FileCallback(String absoluteFilePath){
        if (TextUtils.isEmpty(absoluteFilePath)){
            throw new RuntimeException("请先设置文件的目标路径!!");
        }
        this.absoluteFilePath = absoluteFilePath;
    }

    @Override
    protected ResponseData<String> onSuccess(InputStream inputStream) {
        ResponseData<String> responseData = wrapResponseData();
        File file = new File(absoluteFilePath);
        try {
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            writeInputStreamToFile(inputStream, file);
            if (params.cacheMode != RequestConfig.HttpCacheMode.NO_CACHE) {
                if (HttpRequestAgent.getInstance().getConfig().diskCache != null) {
                    responseData.setData(file.getAbsolutePath());
                    responseData.setFromCache(true);
                    boolean flag = HttpRequestAgent.getInstance().getConfig().diskCache.putData(cacheURL, responseData);
                    if (!flag) {
                        Log.d(getClass().getName(), "response success, but save cache failed !");
                    }
                }
            }
            ResponseData<File> response = wrapResponseData();
            response.setData(file);
            response(response);
        } catch (IOException e) {
            responseData.setParseSuccess(false);
            onError(e);
        }
        return responseData;
    }


    private void response(final ResponseData responseData){
        HttpRequestAgent.getInstance().getConfig().mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(responseData);
            }
        });
    }

    /**
     *
     * @param responseData
     */
    protected abstract void onResponse(ResponseData<File> responseData);


    @Override
    protected boolean isCacheProcessSuccess(ResponseData<String> data) {
        try{
            if (!super.isCacheProcessSuccess(data)){
                return false;
            }
            ResponseData responseData = convertCache(data);
            //判断文件的路径并且文件是否存在
            if (!TextUtils.isEmpty(data.getData())){
                File file = new File(data.getData());
                if (file.exists()){
                    if (data.getData().equals(absoluteFilePath)){
                        responseData.setData(file);
                    }else{
                        File newFile = new File(absoluteFilePath);
                        if (!newFile.getParentFile().exists()){
                            newFile.getParentFile().mkdir();
                        }
                        FileOutputStream fos = new FileOutputStream(newFile);
                        FileInputStream fis = new FileInputStream(file);
                        try{
                            IOUtils.copy(fis, fos);
                            responseData.setData(newFile);
                        }finally {
                            IOUtils.closeQuietly(fos);
                            IOUtils.closeQuietly(fis);
                        }
                    }
                    response(responseData);
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onFailed(Exception exception) {
        response(wrapFailedData(exception));
    }

    /**
     * 为防止文件过大，这个方法不提供使用
     * @param bytes
     */
    @Deprecated
    @Override
    protected void onSuccess(byte[] bytes) {

    }

    private void writeInputStreamToFile(InputStream inputStream, File file) throws IOException{
        FileOutputStream output = new FileOutputStream(file);
        try{
            long count = 0;
            int n = 0;
            byte[] arr = new byte[4196];
            while (-1 != (n = inputStream.read(arr))) {
                output.write(arr, 0, n);
                count += n;

                sendProgress(count, contentLength);
            }
            if (count > Integer.MAX_VALUE) {
                throw new IOException("input stream length error");
            }

        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(output);
        }
    }
}
