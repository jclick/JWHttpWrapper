package cn.jclick.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.callback.Callback;
import cn.jclick.httpwrapper.callback.FileCallback;
import cn.jclick.httpwrapper.callback.ObjectCallback;
import cn.jclick.httpwrapper.callback.ResponseData;
import cn.jclick.httpwrapper.callback.StringCallback;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import cn.jclick.httpwrapper.request.HttpRequestAgent;
import cn.jclick.httpwrapper.request.RequestConfig;
import cn.jclick.httpwrapper.request.RequestParams;
import cn.jclick.httpwrapper.utils.StorageUtils;

public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL = "http://ip.taobao.com/service/getIpInfo2.php";
    private static final String FILE_URL = "https://avatars0.githubusercontent.com/u/3241585?v=3&s=460";

    private File downloadFile;

    private String targetURL;
    private TextView tvCacheResult;
    private TextView tvRequestResult;

    private CheckBox objCb;
    private CheckBox stringCb;
    private CheckBox fileCb;

    private Map<String,  String> requestParams = new HashMap<>();
    private Callback callback;
    private StringCallback stringCallback = new StringCallback() {
        @Override
        protected void onResponse(ResponseData<String> responseData) {
            if (responseData.isSuccess()){
                if (responseData.isFromCache()){
                    tvCacheResult.setText(responseData.toString());
                }else{
                    tvRequestResult.setText(responseData.toString());
                }
            }else{
                Toast.makeText(MainActivity.this, responseData.getDescription(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            //TODO  you can update ui here
        }
    } ;

    private ObjectCallback<DemoResultBean<Location>> objCallback = new ObjectCallback<DemoResultBean<Location>>(new TypeReference<DemoResultBean<Location>>(){}) {
        @Override
        protected void onResponse(ResponseData<DemoResultBean<Location>> responseData) {
            if (responseData.isSuccess()){
                if (responseData.isFromCache()){
                    tvCacheResult.setText(responseData.toString());
                }else{
                    tvRequestResult.setText(responseData.toString());
                }
            }else{
                Toast.makeText(MainActivity.this, responseData.getDescription(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            //TODO  you can update ui here
        }
    };

    private FileCallback fileCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        downloadFile = new File(StorageUtils.getCacheDirectory(this), "test.jpeg");
        targetURL = JSON_URL;
        objCb = (CheckBox) findViewById(R.id.btn_object_callback);
        stringCb = (CheckBox) findViewById(R.id.btn_string_callback);
        fileCb = (CheckBox) findViewById(R.id.btn_file_callback);

        callback = stringCallback;
        tvCacheResult = (TextView) findViewById(R.id.tv_cache_result);
        tvRequestResult = (TextView) findViewById(R.id.tv_request_result);

        HttpRequestAgent.getInstance().init(new RequestConfig.Builder(this).logEnable(true).cacheMode(RequestConfig.HttpCacheMode.NO_CACHE)
                .addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandler(RequestParams params) {
                        //TODO 请求之前的拦截  返回值决定是否继续请求
                        return true;
                    }

                    @Override
                    public void postSuccessHandler(RequestParams params, int statusCode, Map<String, List<String>> headers) {
                        //TODO  请求成功的拦截
                    }

                    @Override
                    public void postFailedHandler(IOException exception) {
                        //TODO 请求失败的拦截器
                    }

                    @Override
                    public void afterCompletion(RequestParams params, ResponseData<String> responseData) {
                        //TODO 请求逻辑处理完毕的回调
                    }
                }).cacheTimeInSeconds(3 * 60).connectionTimeOut(30 *1000).build());


        findViewById(R.id.btn_no_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url(targetURL).cacheMode(RequestConfig.HttpCacheMode.NO_CACHE).get().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_always_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url(targetURL).cacheMode(RequestConfig.HttpCacheMode.ALWAYS_CACHE).get().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_cache_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url(targetURL).cacheMode(RequestConfig.HttpCacheMode.CACHE_FIRST).get().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_cache_no_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url(targetURL).cacheMode(RequestConfig.HttpCacheMode.CACHE_WHEN_NO_NETWORK).get().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_failed_show_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url(targetURL).cacheMode(RequestConfig.HttpCacheMode.FAILED_SHOW_CACHE).get().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });

        objCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams.clear();
                requestParams.put("ip", "221.217.176.144");
                targetURL = JSON_URL;
                objCb.setChecked(true);
                stringCb.setChecked(false);
                fileCb.setChecked(false);
                callback = objCallback;
            }
        });

        stringCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams.clear();
                requestParams.put("ip", "221.217.176.144");
                targetURL = JSON_URL;
                objCb.setChecked(false);
                stringCb.setChecked(true);
                fileCb.setChecked(false);
                callback = stringCallback;
            }
        });

        fileCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams.clear();
                targetURL = FILE_URL;
                objCb.setChecked(false);
                stringCb.setChecked(false);
                fileCb.setChecked(true);
                callback = fileCallback;
            }
        });

        fileCallback = new FileCallback(downloadFile.getAbsolutePath()) {
            @Override
            protected void onResponse(ResponseData<File> responseData) {
                File file = responseData.getData();
                if (responseData.isSuccess()){
                    if (responseData.isFromCache()){
                        tvCacheResult.setText(responseData.toString() + "\n文件最后修改时间" + new Date(file.lastModified()) + "\n文件路径：" + file.getAbsolutePath());
                    }else{
                        tvRequestResult.setText(responseData.toString() + "\n文件最后修改时间" + new Date(file.lastModified()) + "\n文件路径：" + file.getAbsolutePath());
                    }
                }else{
                    Toast.makeText(MainActivity.this, responseData.getDescription(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
