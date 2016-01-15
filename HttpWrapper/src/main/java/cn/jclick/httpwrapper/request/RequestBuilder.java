package cn.jclick.httpwrapper.request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by apple on 16/1/10.
 */
public class RequestBuilder {

    public static RequestBody buildRequestBody(RequestParams params){
        if (params == null){
            return null;
        }
        if (params.requestParams == null || params.requestParams.isEmpty()){
            return RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), "");
        }
        if (params.uploadFiles != null && params.uploadFiles.length > 0){
            return buildFileRequestBody(params);
        }else{
            if (params.mediaType.equals(MultipartBody.FORM.toString())){
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                if (params.requestParams != null){
                    for (String key : params.requestParams.keySet()){
                        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                                RequestBody.create(null, params.requestParams.get(key)));
                    }
                }
                return builder.build();
            }else{
                JSONObject json = new JSONObject();
                for (String key : params.requestParams.keySet()){
                    try {
                        json.put(key, params.requestParams.get(key));
                        RequestBody requestBody = RequestBody.create(MediaType.parse(params.mediaType), json.toString());
                        return requestBody;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
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
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.requestParams.get(key)));
            }
        }
        return builder.build();
    }
}
