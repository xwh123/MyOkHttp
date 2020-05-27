package com.example.myokhtttp.net.interfaces;

import java.io.InputStream;
import java.util.Map;

/**
 * @desc: 结果回调
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 19:02
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 19:02
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IHttpListener {

    void onSuccess(InputStream inputStream,long dataLength);

    void onFaile();

    //增加请求头
    void addHttpHeader(Map<String,String> headerMap);
}
