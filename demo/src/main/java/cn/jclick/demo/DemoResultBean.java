package cn.jclick.demo;

import java.io.Serializable;

/**
 * Created by XuYingjian on 16/1/15.
 */
public class DemoResultBean<T> implements Serializable{

    private int errCode;
    private String message;
    private T result;
}
