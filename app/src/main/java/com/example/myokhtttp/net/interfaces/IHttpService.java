package com.example.myokhtttp.net.interfaces;

import java.util.Map;

/**
 * @desc:  执行请求
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 18:58
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 18:58
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IHttpService {

    //设置url
    void setUrl(String url);

    //执行
    void excute();
    //设置请求数据
    void setRequest(byte[] bytes);

    /**
     * @method   设置回调接口
     * @dec : 方法的作用
     * @author :xuwh
     * @param  : 回调接口
     * @date : 2019/7/27 0027 16:25
     * @UpdateUser:     更新者
     * @UpdateDate:     2019/7/27 0027 16:25
     * @UpdateRemark:   更新说明
     */
    void setHttpcallback(IHttpListener httpListener);


    void pause();

    /**
     * @method  获取请求头的map
     * @dec : 方法的作用
     * @author :xuwh
     * @return : 请求头的map
     * @date : 2019/7/27 0027 18:47
     * @UpdateUser:     更新者
     * @UpdateDate:     2019/7/27 0027 18:47
     * @UpdateRemark:   更新说明
     */
    Map<String,String> getHttpHeadMap();

    //是否取消
    boolean isCancle();

    //取消
    boolean cancle();

    //是否暂停
    boolean isPause();
}
