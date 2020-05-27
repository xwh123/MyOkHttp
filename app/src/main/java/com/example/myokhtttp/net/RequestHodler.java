package com.example.myokhtttp.net;

import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

/**
 * @desc: 请求封装类
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 16:55
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 16:55
 * @UpdateRemark: 更新说明
 * @version:
 */
public class RequestHodler<T> {

    //获取数据  回调结果的类
    private IHttpListener httpListener;
    //执行下载类
    private IHttpService httpService;
    //请求的url
    private String url;
    //请求参数对应的实体
    private T requestInfo;



    public IHttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(IHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(IHttpService httpService) {
        this.httpService = httpService;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(T requestInfo) {
        this.requestInfo = requestInfo;
    }
}
