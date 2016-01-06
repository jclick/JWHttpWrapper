package cn.jclick.httpwrapper.callback;

/**
 * Created by jclick on 16/1/6.
 */
public abstract class CallBack {

    public void beforeStart(){

    }

    public void afterProcess(){

    }

    public void onProgress(int progress){

    }

    public abstract void onRequestCallback();
}
