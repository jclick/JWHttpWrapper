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

import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jclick.httpwrapper.callback.Callback;
import cn.jclick.httpwrapper.callback.ObjectCallback;
import cn.jclick.httpwrapper.callback.ResponseData;
import cn.jclick.httpwrapper.callback.StringCallback;
import cn.jclick.httpwrapper.interceptor.HandlerInterceptor;
import cn.jclick.httpwrapper.request.HttpRequestAgent;
import cn.jclick.httpwrapper.request.RequestConfig;
import cn.jclick.httpwrapper.request.RequestParams;

public class MainActivity extends AppCompatActivity {

    private TextView tvCacheResult;
    private TextView tvRequestResult;

    private CheckBox objCb;
    private CheckBox stringCb;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        objCb = (CheckBox) findViewById(R.id.btn_object_callback);
        stringCb = (CheckBox) findViewById(R.id.btn_string_callback);

        callback = stringCallback;
        requestParams.put("ip", " 221.217.176.144");
        tvCacheResult = (TextView) findViewById(R.id.tv_cache_result);
        tvRequestResult = (TextView) findViewById(R.id.tv_request_result);

        HttpRequestAgent.getInstance().init(new RequestConfig.Builder(this).logEnable(true).cacheMode(RequestConfig.HttpCacheMode.NO_CACHE)
                .baseUrl("http://ip.taobao.com/").addInterceptor(new HandlerInterceptor() {
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
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("service/getIpInfo.php").cacheMode(RequestConfig.HttpCacheMode.NO_CACHE).post().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_always_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("service/getIpInfo.php").cacheMode(RequestConfig.HttpCacheMode.ALWAYS_CACHE).post().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_cache_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("service/getIpInfo.php").cacheMode(RequestConfig.HttpCacheMode.CACHE_FIRST).post().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_cache_no_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("service/getIpInfo.php").cacheMode(RequestConfig.HttpCacheMode.CACHE_WHEN_NO_NETWORK).post().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });
        findViewById(R.id.btn_failed_show_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCacheResult.setText("");
                tvRequestResult.setText("");
                RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("special/time/").cacheMode(RequestConfig.HttpCacheMode.FAILED_SHOW_CACHE).post().build();
                HttpRequestAgent.getInstance().executeRequest(params, callback);
            }
        });

        objCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objCb.setChecked(true);
                stringCb.setChecked(false);
                callback = objCallback;
            }
        });

        stringCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objCb.setChecked(false);
                stringCb.setChecked(true);
                callback = stringCallback;
            }
        });
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
