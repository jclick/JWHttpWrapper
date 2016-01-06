package cn.jclick.httpwrapper.cache;

/**
 * Created by jclick on 16/1/6.
 */
public interface IDiskCache {


    String getDirectory();

    String getString(String cacheKey);

    byte[] getBytes(String cacheKey);

    void putString(String cacheKey, String value);

    void putBytes(String cacheKey, byte[] value);

    void remove(String key);

    void clearAllCache();
}
