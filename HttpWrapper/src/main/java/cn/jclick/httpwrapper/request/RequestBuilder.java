package cn.jclick.httpwrapper.request;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by apple on 16/1/10.
 */
public class RequestBuilder {

    public static RequestBody buildRequestBody(RequestParams params){
        if (params == null){
            return null;
        }
        if (params.uploadFiles != null && params.uploadFiles.length > 0){
            return buildFileRequestBody(params);
        }else{
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(params.mediaType);
            if (params.requestParams != null){
                for (String key : params.requestParams.keySet()){
                    builder.addFormDataPart(key, params.requestParams.get(key));
                }
            }
            return builder.build();
        }
    }

    private static RequestBody buildFileRequestBody(RequestParams params){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (int i = 0; i < params.uploadFiles.length; i ++){
            File file = params.uploadFiles[i];
            FileNameMap map = URLConnection.getFileNameMap();
            String contentType = map.getContentTypeFor(file.getAbsolutePath());
            if (contentType == null){
                contentType = "application/octet-stream";
            }
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse(contentType), file);
            builder.addFormDataPart("file" + i, file.getName(), fileRequestBody);
        }
        if (params.requestParams != null){
            for (String key : params.requestParams.keySet()){
                builder.addFormDataPart(key, params.requestParams.get(key));
            }
        }
        return builder.build();
    }
}
