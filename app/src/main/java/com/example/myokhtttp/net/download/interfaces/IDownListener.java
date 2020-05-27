package com.example.myokhtttp.net.download.interfaces;

import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

/**
 * @desc: 下载回调
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 18:07
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 18:07
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IDownListener extends IHttpListener {

    void setHttpService(IHttpService httpService);

    void setCancleCalle();

    void setPauseCalle();

}
