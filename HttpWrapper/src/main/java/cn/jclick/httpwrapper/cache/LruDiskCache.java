package cn.jclick.httpwrapper.cache;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import cn.jclick.httpwrapper.callback.ResponseData;

public class LruDiskCache implements IDiskCache {

	private static final String TAG = "LruDiskCache";

	public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb

	private static final String ERROR_ARG_NULL = " argument must be not null";
	private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";

	protected DiskLruCache cache;

	protected final FileNameGenerator fileNameGenerator;

	protected int bufferSize = DEFAULT_BUFFER_SIZE;

	/**
	 * @param cacheDir          Directory for file caching
	 * @param fileNameGenerator Name generator for cached files. Generated names must match the regex
	 *                          <strong>[a-z0-9_-]{1,64}</strong>
	 * @param cacheMaxSize      Max cache size in bytes. <b>0</b> means cache size is unlimited.
	 * @throws IOException if cache can't be initialized (e.g. "No space left on device")
	 */
	public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
		if (cacheDir == null) {
			throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
		}
		if (cacheMaxSize < 0) {
			throw new IllegalArgumentException("cacheMaxSize" + ERROR_ARG_NEGATIVE);
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
		}

		if (cacheMaxSize == 0) {
			cacheMaxSize = Long.MAX_VALUE;
		}

		this.fileNameGenerator = fileNameGenerator;
		initCache(cacheDir, cacheMaxSize);
	}

	private void initCache(File cacheDir, long cacheMaxSize)
			throws IOException {
		try {
			cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize);
		} catch (IOException e) {
			throw e; //new RuntimeException("Can't initialize disk cache", e);
		}
	}

	@Override
	public File getDirectory() {
		return cache.getDirectory();
	}

	@Override
	public String getString(String url) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = cache.get(getKey(url));
			if (snapshot == null){
				return null;
			}
			return snapshot.getString(0);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}
	}

	@Override
	public byte[] getBytes(String url) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = cache.get(getKey(url));
			if (snapshot == null){
				return null;
			}
			String result = snapshot.getString(0);
			if (result == null){
				return null;
			}
			return result.getBytes();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}
	}

	@Override
	public boolean putString(String url, String value) throws IOException{
		DiskLruCache.Editor editor = cache.edit(getKey(url));
		if (editor == null) {
			return false;
		}

		BufferedOutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
		boolean savedSuccessfully = true;
		try {
			os.write(value.getBytes());
		} catch (IOException e){
			savedSuccessfully = false;
			e.printStackTrace();
		} finally {
			Util.closeQuietly(os);
		}
		if (savedSuccessfully) {
			editor.commit();
		} else {
			editor.abort();
		}
		return savedSuccessfully;
	}

	@Override
	public boolean putBytes(String url, byte[] value) throws IOException{
		DiskLruCache.Editor editor = cache.edit(getKey(url));
		if (editor == null) {
			return false;
		}

		BufferedOutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
		boolean savedSuccessfully = true;
		try {
			os.write(value);
		} catch (IOException e){
			savedSuccessfully = false;
			e.printStackTrace();
		} finally {
			Util.closeQuietly(os);
		}
		if (savedSuccessfully) {
			editor.commit();
		} else {
			editor.abort();
		}
		return savedSuccessfully;
	}

	@Override
	public boolean remove(String url) {
		try {
			return cache.remove(getKey(url));
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	@Override
	public void clearAllCache() {
		try {
			cache.delete();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		try {
			initCache(cache.getDirectory(), cache.getMaxSize());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public boolean putData(String url, ResponseData<String> responseData) throws IOException{
		String data = JSON.toJSONString(responseData);
		if (TextUtils.isEmpty(data)){
			return false;
		}
		return putString(url, data);
	}

	@Override
	public ResponseData<String> getData(String url) {
		String data = getString(url);
		if (TextUtils.isEmpty(data)){
			return null;
		}
		ResponseData<String> responseData = JSON.parseObject(data, new TypeReference<ResponseData<String>>(){});
		if (responseData == null){
			return null;
		}
		return responseData;
	}

	public void close() {
		try {
			cache.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		cache = null;
	}

	private String getKey(String imageUri) {
		return fileNameGenerator.generate(imageUri);
	}
}