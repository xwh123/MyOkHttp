package com.example.myokhtttp.net;

import com.example.myokhtttp.net.interfaces.IHttpCallBack;
import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.util.concurrent.FutureTask;

/**
 * @desc: json请求 调用
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 20:53
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 20:53
 * @UpdateRemark: 更新说明
 * @version:
 */
public class Velloy {

    /**
     * @method  发送json 请求  T:请求参数类型   M：响应参数类型
     * @dec : 方法的作用
     * @author :xuwh
     * @param  : requestInfo：请求参数  url：请求地址  responce：解析对象  iHttpCallBack：回调对象
     * @return :
     * @date : 2019/7/26 0026 20:56
     * @UpdateUser:     更新者
     * @UpdateDate:     2019/7/26 0026 20:56
     * @UpdateRemark:   更新说明
     */
    public static <T, M> void sendJsonRequest(T requestInfo, String url, Class<M> responce, IHttpCallBack iHttpCallBack) {

        //策略模式
        IHttpService iHttpService = new JsonHttpService();
        IHttpListener iHttpListener = new JsonHttpListener<>(iHttpCallBack,responce);
        RequestHodler requestHodler = new RequestHodler();
        requestHodler.setUrl(url);
        requestHodler.setHttpListener(iHttpListener);
        requestHodler.setHttpService(iHttpService);
        requestHodler.setRequestInfo(requestInfo);

        HttpTask httpTask = new HttpTask(requestHodler);
        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        try {
            threadPoolManager.exectu(new FutureTask<Object>(httpTask,null));
        } catch (InterruptedException e) {
            iHttpListener.onFaile();
            e.printStackTrace();
        }
    }
}
