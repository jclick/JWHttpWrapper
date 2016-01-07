package cn.jclick.httpwrapper.request;

/**
 * Created by XuYingjian on 16/1/6.
 */
public class HttpRequestAgent {

    private static HttpRequestAgent INSTANCE;

    private String baseUrl;
    private long connectionTimeOut;
    private long maxRetryTimes;
    private long maxConnections;

    private HttpRequestAgent(){
    }

    public static HttpRequestAgent getInstance(){
        if (INSTANCE == null) {
            synchronized (HttpRequestAgent.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpRequestAgent();
                }
            }
        }
        return INSTANCE;
    }

    public void executeRequest(BaseRequestClient requestClient){

    }

    public void interruptRequestByTag(Object ...tag){

    }

    public void interruptAllRequest(){

    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public long getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(long connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public long getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(long maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public long getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(long maxConnections) {
        this.maxConnections = maxConnections;
    }
}
