package cn.jclick.demo;

import java.io.Serializable;

/**
 * Created by XuYingjian on 16/1/15.
 */
public class DemoResultBean<T> implements Serializable{

    private int code;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DemoResultBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
