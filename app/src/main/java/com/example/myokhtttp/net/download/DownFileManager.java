package com.example.myokhtttp.net.download;

import android.app.IntentService;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.BaseAdapter;

import com.example.myokhtttp.LogUtil;
import com.example.myokhtttp.net.HttpTask;
import com.example.myokhtttp.net.RequestHodler;
import com.example.myokhtttp.net.ThreadPoolManager;
import com.example.myokhtttp.net.download.dao.DownLoadDao;
import com.example.myokhtttp.net.download.db.BaseDaoFactory;
import com.example.myokhtttp.net.download.enums.DownloadStatus;
import com.example.myokhtttp.net.download.enums.DownloadStopMode;
import com.example.myokhtttp.net.download.enums.Priority;
import com.example.myokhtttp.net.download.interfaces.IDownloadCallable;
import com.example.myokhtttp.net.download.interfaces.IDownloadServiceCallable;
import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;

/**
 * @desc: 下载文件管理
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 18:04
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 18:04
 * @UpdateRemark: 更新说明
 * @version:
 */
public class DownFileManager implements IDownloadServiceCallable {

    //    private static

    private byte[] lock = new byte[0];


    DownLoadDao downLoadDao = BaseDaoFactory.getInstance().
            getDataHelper(DownLoadDao.class, DownLoadItemInfo.class);

    /**
     * 观察者模式
     */
    private final List<IDownloadCallable> applisteners = new CopyOnWriteArrayList<IDownloadCallable>();


    /**
     * 正在下载的所有任务
     */
    private static List<DownLoadItemInfo> downloadFileTaskList = new CopyOnWriteArrayList();

    private Handler handler = new Handler(Looper.getMainLooper());

    //可以精确到秒  2017-4-16 12:43:37
    private DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public int download(String url) {
        String[] preFix = url.split("/");
        return this.download(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + preFix[preFix.length - 1]);
    }

    public int download(String url, String filePath) {
        String[] preFix = url.split("/");
        String displayName = preFix[preFix.length - 1];
        return this.download(url, filePath, displayName);
    }

    public int download(String url, String filePath, String displayName) {
        return this.download(url, filePath, displayName, Priority.middle);
    }

    public int download(String url, String filePath, String displayName, Priority priority) {
        if (priority == null) {
            priority = Priority.low;
        }
        File file = new File(filePath);
        DownLoadItemInfo downloadItemInfo = null;

        downloadItemInfo = downLoadDao.findRecord(url, filePath);
        //没下载
        if (downloadItemInfo == null) {
            /**
             * 根据文件路径查找
             */
            List<DownLoadItemInfo> samesFile = downLoadDao.findRecord(filePath);
            /**
             * 大于0  表示下载
             */
            if (samesFile.size() > 0) {
                DownLoadItemInfo sameDown = samesFile.get(0);
                if (sameDown.getCurrentLen() == sameDown.getTotalLen()) {
                    synchronized (applisteners) {
                        for (IDownloadCallable downloadCallable : applisteners) {
                            downloadCallable.onDownloadError(sameDown.getId(), 2, "文件已经下载了");
                        }
                    }

                }
            }
            /**---------------------------------------------
             * 插入数据库
             * 可能插入失败
             * 因为filePath  和id是独一无二的  在数据库建表时已经确定了
             */
            int recrodId = downLoadDao.addRecrod(url, filePath, displayName, priority.getValue());
            if (recrodId != -1) {
                synchronized (applisteners) {
                    for (IDownloadCallable downloadCallable : applisteners) {
                        //通知应用层  数据库被添加了
                        downloadCallable.onDownloadInfoAdd(downloadItemInfo.getId());
                    }
                }
            }
            //插入失败时，再次进行查找，确保能查得到
            else {
                //插入
                downloadItemInfo = downLoadDao.findRecord(url, filePath);
            }
        }
        /**-----------------------------------------------
         *
         * 是否正在下载`
         */
        if (isDowning(file.getAbsolutePath())) {
            synchronized (applisteners) {
                for (IDownloadCallable downloadCallable : applisteners) {
                    downloadCallable.onDownloadError(downloadItemInfo.getId(), 4, "正在下载，请不要重复添加");
                }
            }
            return downloadItemInfo.getId();
        }

        if (downloadItemInfo != null) {
            downloadItemInfo.setPriority(priority.getValue());
            //添加----------------------------------------------------
            downloadItemInfo.setStopMode(DownloadStopMode.auto.getValue());

            //判断数据库存的 状态是否是完成
            if (downloadItemInfo.getStatus() != DownloadStatus.finish.getValue()) {
                if (downloadItemInfo.getTotalLen() == 0L || file.length() == 0L) {
                    LogUtil.w("还未开始下载");
                    //----------------------删除--------------------
                    downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                }
                //判断数据库中 总长度是否等于文件长度
                if (downloadItemInfo.getTotalLen() == file.length() && downloadItemInfo.getTotalLen() != 0) {
                    downloadItemInfo.setStatus(DownloadStatus.finish.getValue());
                    synchronized (applisteners) {
                        for (IDownloadCallable downloadCallable : applisteners) {
                            try {
                                downloadCallable.onDownloadError(downloadItemInfo.getId(), 4, "已经下载了");
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            //------------------添加--------
            else {
                if (!file.exists() || (downloadItemInfo.getTotalLen() != downloadItemInfo.getCurrentLen())) {
                    downloadItemInfo.setStatus(DownloadStatus.failed.getValue());
                }
            }
            /**
             *
             * 更新
             */
            downLoadDao.updateRecord(downloadItemInfo);
            //移到括号里面来----------------------------------------------------
            /**
             * 判断是否已经下载完成
             */
            if (downloadItemInfo.getStatus() == DownloadStatus.finish.getValue()) {
                LogUtil.w("已经下载完成  回调应用层");
                final int downId = downloadItemInfo.getId();
                synchronized (applisteners) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (IDownloadCallable downloadCallable : applisteners) {
                                downloadCallable.onDownloadStatusChanged(downId, DownloadStatus.finish);
                            }
                        }
                    });
                }
                downLoadDao.removeRecordFromMemery(downId);
                return downloadItemInfo.getId();
            }//之前的下载 状态为暂停状态
            List<DownLoadItemInfo> allDowning = downloadFileTaskList;
            //当前下载不是最高级  则先退出下载
            if (priority != Priority.high) {
                for (DownLoadItemInfo downling : allDowning) {
                    //从下载表中  获取到全部正在下载的任务
                    downling = downLoadDao.findSigleRecord(downling.getFilePath());

                    if (downling != null && downling.getPriority() == Priority.high.getValue()) {

                        /**
                         *     更改---------
                         *     当前下载级别不是最高级 传进来的是middle    但是在数据库中查到路径一模一样 的记录   所以他也是最高级------------------------------
                         *     比如 第一次下载是用最高级下载，app闪退后，没有下载完成，第二次传的是默认级别，这样就应该是最高级别下载

                         */
                        if (downling.getFilePath().equals(downloadItemInfo.getFilePath())) {
                            break;
                        } else {
                            return downloadItemInfo.getId();
                        }
                    }
                }
            }
            //
            down(downloadItemInfo);
            if (priority == Priority.high || priority == Priority.middle) {
                synchronized (allDowning) {
                    for (DownLoadItemInfo downloadItemInfo1 : allDowning) {
                        if (!downloadItemInfo.getFilePath().equals(downloadItemInfo1.getFilePath())) {
                            DownLoadItemInfo downingInfo = downLoadDao.findSigleRecord(downloadItemInfo1.getFilePath());
                            if (downingInfo != null) {
                                pause(downloadItemInfo.getId(), DownloadStopMode.auto);
                            }
                        }
                    }
                }
                return downloadItemInfo.getId();
            }

        }

        return -1;
    }

    /**
     * 停止
     *
     * @param downloadId
     * @param mode
     */
    public void pause(int downloadId, DownloadStopMode mode) {
        if (mode == null) {
            mode = DownloadStopMode.auto;
        }
        final DownLoadItemInfo downloadInfo = downLoadDao.findRecordById(downloadId);
        if (downloadInfo != null) {
            // 更新停止状态
            if (downloadInfo != null) {
                downloadInfo.setStopMode(mode.getValue());
                downloadInfo.setStatus(DownloadStatus.pause.getValue());
                downLoadDao.updateRecord(downloadInfo);
            }
            for (DownLoadItemInfo downing : downloadFileTaskList) {
                if (downloadId == downing.getId()) {
                    downing.getHttpTask().pause();
                }
            }
        }
    }

    /**
     * 判断当前是否正在下载
     *
     * @param absolutePath
     * @return
     */
    private boolean isDowning(String absolutePath) {
        for (DownLoadItemInfo downloadItemInfo : downloadFileTaskList) {
            if (downloadItemInfo.getFilePath().equals(absolutePath)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param :
     * @return :
     * @method 添加观察者
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/28 0028 17:52
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/28 0028 17:52
     * @UpdateRemark: 更新说明
     */
    public void setDownCallable(IDownloadCallable iDownloadCallable) {
        synchronized (applisteners) {
            applisteners.add(iDownloadCallable);
        }
    }

    public DownLoadItemInfo down(DownLoadItemInfo downLoadItemInfo) {

        synchronized (lock) {

            RequestHodler requestHodler = new RequestHodler();
            //设置请求下载的策略
            IHttpService httpService = new FileDownHttpService();
            //得到请求头的参数  map
            Map<String, String> httpHeadMap = httpService.getHttpHeadMap();
            //处理结果的策略
            IHttpListener httpListener = new DownLoadListener(downLoadItemInfo, this, httpService);

            requestHodler.setHttpListener(httpListener);

            requestHodler.setHttpService(httpService);

            requestHodler.setUrl(downLoadItemInfo.getUrl());

            HttpTask httpTask = new HttpTask(requestHodler);
            downLoadItemInfo.setHttpTask(httpTask);

            //添加缓存
            downloadFileTaskList.add(downLoadItemInfo);
            httpTask.start();

        }

        return downLoadItemInfo;
    }

    @Override
    public void onDownloadStatusChanged(DownLoadItemInfo downloadItemInfo) {

        DownLoadItemInfo sigleRecord = downLoadDao.findSigleRecord(downloadItemInfo.getFilePath());
        if (null != sigleRecord) {
            sigleRecord.setStatus(downloadItemInfo.getStatus());
            downLoadDao.updateRecord(sigleRecord);
            synchronized (applisteners) {
                for (IDownloadCallable downloadCallable : applisteners) {
                    try {
                        downloadCallable.onDownloadStatusChanged(sigleRecord.getId(), DownloadStatus.downloading);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }


    }

    @Override
    public void onTotalLengthReceived(DownLoadItemInfo downloadItemInfo) {
        DownLoadItemInfo loadItemInfo = downLoadDao.findSigleRecord(downloadItemInfo.getFilePath());
        if (null != loadItemInfo) {
            loadItemInfo.setTotalLen(downloadItemInfo.getTotalLen());
            downLoadDao.updateRecord(loadItemInfo);
            synchronized (applisteners) {
                for (IDownloadCallable downloadCallable : applisteners) {
                    try {
                        downloadCallable.onTotalLengthReceived(loadItemInfo.getId(), downloadItemInfo.getTotalLen());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    @Override
    public void onCurrentSizeChanged(DownLoadItemInfo downloadItemInfo, double downLenth, long speed) {
        LogUtil.w("下载速度：" + speed / 1000 + "k/s");
        LogUtil.i("-----路径  " + downloadItemInfo.getFilePath() + "  下载长度  " + downLenth + "   速度  " + speed);
        DownLoadItemInfo loadItemInfo = downLoadDao.findSigleRecord(downloadItemInfo.getFilePath());
        if (null != loadItemInfo) {
            synchronized (applisteners) {
                for (IDownloadCallable downloadCallable : applisteners) {
                    try {
                        downloadCallable.onCurrentSizeChanged(loadItemInfo.getId(), downLenth, speed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    @Override
    public void onDownloadSuccess(DownLoadItemInfo downloadItemInfo) {
        LogUtil.w("下载成功    路径  " + downloadItemInfo.getFilePath() + "  url " + downloadItemInfo.getUrl());
        DownLoadItemInfo loadItemInfo = downLoadDao.findSigleRecord(downloadItemInfo.getFilePath());
        if (null != loadItemInfo) {
            loadItemInfo.setCurrentLen(new File(downloadItemInfo.getFilePath()).length());
            loadItemInfo.setFinishTime(dateFormat.format(new Date()));
            loadItemInfo.setStopMode(DownloadStopMode.hand.getValue());
            loadItemInfo.setStatus(DownloadStatus.finish.getValue());
            downLoadDao.updateRecord(loadItemInfo);

            synchronized (applisteners) {
                for (IDownloadCallable downloadCallable : applisteners) {
                    try {
                        downloadCallable.onDownloadSuccess(loadItemInfo.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        resumeAutoCancelItem();
    }

    //恢复全部下载
    private void resumeAutoCancelItem() {
        List<DownLoadItemInfo> allAutoCancelList = downLoadDao.findAllAutoCancelRecords();
        if (null == allAutoCancelList || 0 == allAutoCancelList.size()) {
            return;
        }

        List<DownLoadItemInfo> notDownloadingList = new ArrayList<DownLoadItemInfo>();
        for (DownLoadItemInfo loadItemInfo : allAutoCancelList) {
            if (!isDowning(loadItemInfo.getFilePath())) {
                notDownloadingList.add(loadItemInfo);
            }
        }

        for (DownLoadItemInfo downLoadItemInfo : notDownloadingList) {
            if (downLoadItemInfo.getPriority() == Priority.high.getValue()) {
                resumeItem(downLoadItemInfo.getId(), Priority.high);
                return;
            } else if (downLoadItemInfo.getPriority() == Priority.middle.getValue()) {
                resumeItem(downLoadItemInfo.getId(), Priority.middle);
                return;
            }
        }

    }

    //恢复下载
    private void resumeItem(int downId, Priority priority) {
        DownLoadItemInfo downLoadItemInfo = downLoadDao.findRecordById(downId);
        if (null == downLoadItemInfo) {
            return;
        }
        if (null == priority) {
            priority = Priority.getInstance(downLoadItemInfo.getPriority() == null ? Priority.low.getValue() : Priority.middle.getValue());
        }

        File file = new File(downLoadItemInfo.getFilePath());

        downLoadItemInfo.setStopMode(DownloadStopMode.auto.getValue());

        downLoadDao.updateRecord(downLoadItemInfo);
        download(downLoadItemInfo.getUrl(), file.getPath(), null, priority);
    }

    @Override
    public void onDownloadPause(DownLoadItemInfo downloadItemInfo) {

    }

    @Override
    public void onDownloadError(DownLoadItemInfo downloadItemInfo, int var2, String var3) {
    }
}
