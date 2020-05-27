package com.example.myokhtttp.net.download.interfaces;

import com.example.myokhtttp.net.download.DownLoadItemInfo;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 18:15
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 18:15
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IDownloadServiceCallable {

    void onDownloadStatusChanged(DownLoadItemInfo downloadItemInfo);

    void onTotalLengthReceived(DownLoadItemInfo downloadItemInfo);

    void onCurrentSizeChanged(DownLoadItemInfo downloadItemInfo, double downLenth, long speed);

    void onDownloadSuccess(DownLoadItemInfo downloadItemInfo);

    void onDownloadPause(DownLoadItemInfo downloadItemInfo);

    void onDownloadError(DownLoadItemInfo downloadItemInfo, int var2, String var3);

}
