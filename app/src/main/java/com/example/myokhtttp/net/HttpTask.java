package com.example.myokhtttp.net;

import com.alibaba.fastjson.JSON;
import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.FutureTask;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 19:03
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 19:03
 * @UpdateRemark: 更新说明
 * @version:
 */
public class HttpTask<T> implements Runnable {

    IHttpService iHttpService;

    private FutureTask futureTask;

    public HttpTask(RequestHodler<T> requestHodler) {
        this.iHttpService = requestHodler.getHttpService();
        //设置回调
        iHttpService.setHttpcallback(requestHodler.getHttpListener());
        iHttpService.setUrl(requestHodler.getUrl());
        requestHodler.getHttpListener().addHttpHeader(iHttpService.getHttpHeadMap());

        try {
            //设置请求参数 json
            if (null != requestHodler.getRequestInfo()) {
                String jsonContent = JSON.toJSONString(requestHodler.getRequestInfo());
                this.iHttpService.setRequest(jsonContent.getBytes("utf-8"));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //执行网络请求
        iHttpService.excute();
    }

    //开始
    public void start(){
        futureTask = new FutureTask(this,null);
        try {
            ThreadPoolManager.getInstance().exectu(futureTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //暂停
    public void pause(){
        iHttpService.pause();
        if (null!=futureTask){
            ThreadPoolManager.getInstance().removeTask(futureTask);
        }
    }
}
