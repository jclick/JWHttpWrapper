
### JWHttpWrapper 主要功能
1. 基于OKHttp的网络库，底层依赖很小，只有一个文件依赖于OKhttp， 可以快速切换为其它网络库。
2. 支持本地文件缓存以及多样化的缓存方式。
3. 自定义Interceptor。可以拦截数据进行处理
4. 请求返回数据处理在单独现成进行，提供的回调方法在UI现成进行，可以直接用返回的数据更新UI

###  使用说明
- 初始化
  
```java
RequestConfig config = new RequestConfig.Builder(this).logEnable(true).cacheMode(RequestConfig.HttpCacheMode.NO_CACHE)
                .baseUrl("http://ip.taobao.com/").cacheTimeInSeconds(3 * 60).connectionTimeOut(30 *1000).build();
HttpRequestAgent.getInstance().init(config);
```

- 发送请求
```java
RequestParams params = new RequestParams.Builder().requestParams(requestParams).url("service/getIpInfo.php").cacheMode(RequestConfig.HttpCacheMode.ALWAYS_CACHE).post().build();
HttpRequestAgent.getInstance().executeRequest(params, callback);
```
- 缓存设置
```java
ALWAYS_CACHE,//缓存时间内，不发请求，直接返回缓存结果
CACHE_FIRST,//优先返回缓存结果，然后发送请求.（总共返回二次数据）
FAILED_SHOW_CACHE,//请求失败后展示缓存
CACHE_WHEN_NO_NETWORK//没有网络的时候展示缓存
```
- Interceptor使用
```java
RequestConfig config = new RequestConfig.Builder(this).logEnable(true).cacheMode(RequestConfig.HttpCacheMode.NO_CACHE)
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
```
- 返回数据
> 返回数据结果为ResponseData类型, 可以对象序列化直接返回JavaBean,只需要设置Callback为ObjectCallback，例如：

```java
 ObjectCallback<DemoResultBean<Location>> objCallback = new ObjectCallback<DemoResultBean<Location>>(new TypeReference<DemoResultBean<Location>>(){}) {
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
    }
```
