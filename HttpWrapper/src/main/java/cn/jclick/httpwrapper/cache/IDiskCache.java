package cn.jclick.httpwrapper.cache;

import java.io.File;
import java.io.IOException;

import cn.jclick.httpwrapper.callback.ResponseData;

/**
 * Created by jclick on 16/1/6.
 */
public interface IDiskCache {


    File getDirectory();

    String getString(String url);

    byte[] getBytes(String url);

    ResponseData<String> getData(String url);

    boolean putData(String url, ResponseData<String> responseData) throws IOException;

    boolean putString(String url, String value) throws IOException;

    boolean putBytes(String url, byte[] value) throws IOException;

    boolean remove(String url);

    void clearAllCache();
}
