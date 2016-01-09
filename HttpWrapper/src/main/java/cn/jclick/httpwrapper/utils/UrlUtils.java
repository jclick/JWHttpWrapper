package cn.jclick.httpwrapper.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.jclick.httpwrapper.request.RequestParams;

/**
 * Created by apple on 16/1/9.
 */
public class UrlUtils {

    public static final String TAG = "UrlUtils";

    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, RequestParams params) {
        if (url == null)
            return null;
        if (shouldEncodeUrl) {
            try {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                URL _url = new URL(decodedURL);
                URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
                url = _uri.toASCIIString();
            } catch (Exception ex) {
                Log.e(TAG, "getUrlWithQueryString encoding URL", ex);
            }
        }

        if (params.requestParams != null) {
            String paramString = "";
            for (String key : params.requestParams.keySet()){
                paramString += key + "&" + params.requestParams.get(key).toString();
            }

            if (shouldEncodeUrl){
                try {
                    paramString = URLEncoder.encode(paramString, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (!paramString.equals("") && !paramString.equals("?")) {
                url += url.contains("?") ? "&" : "?";
                url += paramString;
            }
        }
        return url;
    }
}
