package com.example.myokhtttp.net.download;

import android.os.Handler;
import android.os.Looper;

import com.example.myokhtttp.net.download.enums.DownloadStatus;
import com.example.myokhtttp.net.download.interfaces.IDownListener;
import com.example.myokhtttp.net.download.interfaces.IDownloadServiceCallable;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @desc: 下载的回调
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 18:10
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 18:10
 * @UpdateRemark: 更新说明
 * @version:
 */
public class DownLoadListener implements IDownListener {

    private DownLoadItemInfo downLoadItemInfo;

    private File file;

    protected String url;

    //记录下载开始点
    private long breakPoint;

    //下载回调
    private IDownloadServiceCallable downloadServiceCallable;


    private IHttpService httpService;

    //得到主线程
    private Handler handler = new Handler(Looper.getMainLooper());

    public DownLoadListener(DownLoadItemInfo downLoadItemInfo, IDownloadServiceCallable iDownloadServiceCallable, IHttpService httpService) {
        this.downLoadItemInfo = downLoadItemInfo;
        this.downloadServiceCallable = iDownloadServiceCallable;
        this.httpService = httpService;

        this.file = new File(downLoadItemInfo.getFilePath());
        //获取已下载文件的长度
        this.breakPoint = file.length();

    }

    //设置断点下载请求头
    public void addHttpHead(Map<String, String> headMap) {
        long length = getFile().length();
        if (length > 0L) {
            headMap.put("Range", "bytes=" + length + "-");
        }
    }

    public DownLoadListener(DownLoadItemInfo downLoadItemInfo) {
        this.downLoadItemInfo = downLoadItemInfo;
    }

    @Override
    public void setHttpService(IHttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public void setCancleCalle() {

    }

    @Override
    public void setPauseCalle() {

    }

    @Override
    public void onSuccess(InputStream inputStream, long dataLength) {
        long startTime = System.currentTimeMillis();
        //用于计算每秒多少K
        long speed = 0L;
        //花费时间
        long useTime = 0L;
        //下载的长度
        long getLen = 0L;
        //接受的长度
        long receiveLen = 0L;
        boolean bufferLen = false;
        //单位时间下载的字节数
        long calcSpeedLen = 0L;
        //总数
        long totalLength = this.breakPoint + dataLength;
        //更新数量
        this.receviceTotalLength(totalLength);
        //更新状态
        this.downloadStatusChange(DownloadStatus.downloading);
        byte[] buffer = new byte[512];
        int count = 0;
        long currentTime = System.currentTimeMillis();
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            if (!makeDir(this.getFile().getParentFile())) {
                downloadServiceCallable.onDownloadError(downLoadItemInfo, 1, "创建文件夹失败");
            } else {
                fos = new FileOutputStream(this.getFile(), true);
                bos = new BufferedOutputStream(fos);
                int length = 1;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (this.getHttpService().isCancle()) {
                        downloadServiceCallable.onDownloadError(downLoadItemInfo, 1, "用户取消了");
                        return;
                    }

                    if (this.getHttpService().isPause()) {
                        downloadServiceCallable.onDownloadError(downLoadItemInfo, 2, "用户暂停了");
                        return;
                    }
                    bos.write(buffer, 0, length);
                    getLen += (long) length;
                    receiveLen += (long) length;
                    calcSpeedLen += (long) length;
                    ++count;
                    if (receiveLen * 10L / totalLength >= 1L || count >= 5000) {
                        currentTime = System.currentTimeMillis();
                        useTime = currentTime - startTime;
                        startTime = currentTime;
                        speed = 1000L * calcSpeedLen / useTime;
                        count = 0;
                        calcSpeedLen = 0L;
                        receiveLen = 0L;
                        //应该保存数据库
                        this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    }
                }
                bos.close();
                inputStream.close();
                if (dataLength != getLen) {
                    downloadServiceCallable.onDownloadError(downLoadItemInfo, 3, "下载长度不相等");
                } else {
                    this.downloadLengthChange(this.breakPoint + getLen, totalLength, speed);
                    this.downloadServiceCallable.onDownloadSuccess(downLoadItemInfo.copy());
                }
            }
        } catch (IOException ioException) {
            if (this.getHttpService() != null) {
                //                this.getHttpService().abortRequest();
            }
            return;
        } catch (Exception e) {
            if (this.getHttpService() != null) {
                //                this.getHttpService().abortRequest();
            }
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * @param :
     * @return :
     * @method 更改下载时的状态
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/27 0027 19:29
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/27 0027 19:29
     * @UpdateRemark: 更新说明
     */
    private void downloadStatusChange(DownloadStatus downloading) {
        downLoadItemInfo.setStatus(downloading.getValue());
        final DownLoadItemInfo copyDownLoadItemInfo = downLoadItemInfo.copy();
        if (null != downloadServiceCallable) {
            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onDownloadStatusChanged(copyDownLoadItemInfo);
                    }
                });
            }
        }
    }

    /**
     * @param :
     * @return :
     * @method 回调文件下载长度变化
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/27 0027 19:29
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/27 0027 19:29
     * @UpdateRemark: 更新说明
     */
    private void downloadLengthChange(final long downLength, final long totalLength, final long speed) {
        downLoadItemInfo.setCurrentLength(downLength);
        if (null != downloadServiceCallable) {

            final DownLoadItemInfo copyDownLoadItemInfo = downLoadItemInfo.copy();

            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onCurrentSizeChanged(copyDownLoadItemInfo, downLength / totalLength, speed);
                    }
                });
            }
        }
    }

    /**
     * 回调  长度的变化
     *
     * @param totalLength
     */
    private void receviceTotalLength(long totalLength) {
        downLoadItemInfo.setCurrentLength(totalLength);
        final DownLoadItemInfo copyDownloadItemInfo = downLoadItemInfo.copy();
        if (downloadServiceCallable != null) {
            synchronized (this.downloadServiceCallable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallable.onTotalLengthReceived(copyDownloadItemInfo);
                    }
                });
            }
        }

    }


    /**
     * 创建文件夹的操作
     *
     * @param parentFile
     * @return
     */
    private boolean makeDir(File parentFile) {
        return parentFile.exists() && !parentFile.isFile()
                ? parentFile.exists() && parentFile.isDirectory() :
                parentFile.mkdirs();
    }


    @Override
    public void onFaile() {

    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public File getFile() {
        return file;
    }
}
