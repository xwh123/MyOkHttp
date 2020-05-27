package com.example.myokhtttp.net.interfaces;

/**
 * @desc: 回调调用层的接口
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 20:33
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 20:33
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IHttpCallBack<M> {
    void onSuccess(M m);
    void onFaile();
}
