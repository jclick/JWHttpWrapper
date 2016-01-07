package cn.jclick.httpwrapper.cache;

import java.io.File;
import java.io.IOException;

/**
 * Created by jclick on 16/1/6.
 */
public interface IDiskCache {


    File getDirectory();

    String getString(String url);

    byte[] getBytes(String url);

    boolean putString(String url, String value) throws IOException;

    boolean putBytes(String url, byte[] value) throws IOException;

    boolean remove(String url);

    void clearAllCache();
}
