package com.example.myokhtttp.net.download.dao;

import android.database.Cursor;

import com.example.myokhtttp.net.download.DownLoadItemInfo;
import com.example.myokhtttp.net.download.enums.DownloadStatus;
import com.example.myokhtttp.net.download.db.BaseDao;
import com.example.myokhtttp.net.download.enums.DownloadStopMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @desc: 下载数据库操作
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/28 0028 16:47
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/28 0028 16:47
 * @UpdateRemark: 更新说明
 * @version:
 */
public class DownLoadDao extends BaseDao<DownLoadItemInfo> {

    //保存应该下载的集合  不包括已经下载成功的
    private List<DownLoadItemInfo> downLoadItemInfos = Collections.synchronizedList(new ArrayList<DownLoadItemInfo>());

    private DownloadInfoComparator downloadInfoComparator = new DownloadInfoComparator();

    @Override
    public String createTable() {
        return "create table if not exists  t_downloadInfo(" + "id Integer primary key, " +
                "url TEXT not null," + "filePath TEXT not null, " + "displayName TEXT, " +
                "status Integer, " + "totalLen Long, " + "currentLen Long," + "startTime TEXT,"
                + "finishTime TEXT," + "userId TEXT, " + "httpTaskType TEXT," + "priority  Integer,"
                + "stopMode Integer," + "downloadMaxSizeKey TEXT," + "unique(filePath))";
    }

    @Override
    public List<DownLoadItemInfo> query(String sql) {
        return null;
    }

    //查找根据下载优先级 自动推出的下载记录
    public List<DownLoadItemInfo> findAllAutoCancelRecords() {

        List<DownLoadItemInfo> resultList = new ArrayList<DownLoadItemInfo>();
        synchronized (DownLoadDao.class) {
            DownLoadItemInfo downLoadItemInfo = null;
            for (int i = 0; i < downLoadItemInfos.size(); i++) {
                downLoadItemInfo = downLoadItemInfos.get(i);
                if (downLoadItemInfo.getStatus() != DownloadStatus.failed.getValue() &&
                        downLoadItemInfo.getStatus() == DownloadStatus.pause.getValue() &&
                        downLoadItemInfo.getStopMode() == DownloadStopMode.auto.getValue()) {
                    resultList.add(downLoadItemInfo);
                }
            }

        }
        if (!resultList.isEmpty()) {
            Collections.sort(resultList, downloadInfoComparator);
        }
        return resultList;
    }

    /**
     * 比较器
     */
    class DownloadInfoComparator implements Comparator<DownLoadItemInfo> {
        @Override
        public int compare(DownLoadItemInfo lhs, DownLoadItemInfo rhs) {
            return rhs.getId() - lhs.getId();
        }
    }

    /**
     * id
     */
    /**
     * 生成下载id
     *
     * @return 返回下载id
     */
    private Integer generateRecordId() {
        int maxId = 0;
        String sql = "select max(id)  from " + getTableName();
        synchronized (DownLoadDao.class) {
            Cursor cursor = this.sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                String[] colmName = cursor.getColumnNames();

                int index = cursor.getColumnIndex("max(id)");
                if (index != -1) {
                    Object value = cursor.getInt(index);
                    if (value != null) {
                        maxId = Integer.parseInt(String.valueOf(value));
                    }
                }
            }

        }
        return maxId + 1;
    }

    /**
     * 根据下载地址和下载文件路径查找下载记录
     *
     * @param url      下载地址
     * @param filePath 下载文件路径
     * @return
     */
    public DownLoadItemInfo findRecord(String url, String filePath) {
        synchronized (DownLoadDao.class) {
            for (DownLoadItemInfo record : downLoadItemInfos) {
                if (record.getUrl().equals(url) && record.getFilePath().equals(filePath)) {
                    return record;
                }
            }
            /**
             * 内存集合找不到
             * 就从数据库中查找
             */
            DownLoadItemInfo where = new DownLoadItemInfo();
            where.setUrl(url);
            where.setFilePath(filePath);
            List<DownLoadItemInfo> resultList = super.query(where);
            if (resultList.size() > 0) {
                return resultList.get(0);
            }
            return null;
        }

    }

    /**
     * 根据 下载文件路径查找下载记录
     * <p>
     * 下载地址
     *
     * @param filePath 下载文件路径
     * @return
     */
    public List<DownLoadItemInfo> findRecord(String filePath) {
        synchronized (DownLoadDao.class) {
            DownLoadItemInfo where = new DownLoadItemInfo();
            where.setFilePath(filePath);
            List<DownLoadItemInfo> resultList = super.query(where);
            return resultList;
        }

    }

    /**
     * 添加下载记录
     *
     * @param url         下载地址
     * @param filePath    下载文件路径
     * @param displayName 文件显示名
     * @param priority    小组优先级
     *                    TODO
     * @return 下载id
     */
    public int addRecrod(String url, String filePath, String displayName, int priority) {
        synchronized (DownLoadDao.class) {
            DownLoadItemInfo existDownloadInfo = findRecord(url, filePath);
            if (existDownloadInfo == null) {
                DownLoadItemInfo record = new DownLoadItemInfo();
                record.setId(generateRecordId());
                record.setUrl(url);
                record.setFilePath(filePath);
                record.setDisplayName(displayName);
                record.setStatus(DownloadStatus.waitting.getValue());
                record.setTotalLen(0L);
                record.setCurrentLen(0L);
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                record.setStartTime(dateFormat.format(new Date()));
                record.setFinishTime("0");
                record.setPriority(priority);
                super.insert(record);
                downLoadItemInfos.add(record);
                return record.getId();
            }
            return -1;
        }
    }

    /**
     * 更新下载记录
     *
     * @param record 下载记录
     * @return
     */
    public int updateRecord(DownLoadItemInfo record) {
        DownLoadItemInfo where = new DownLoadItemInfo();
        where.setId(record.getId());
        int result = 0;
        synchronized (DownLoadDao.class) {
            try {
                result = super.update(record, where);
            } catch (Throwable e) {
            }
            if (result > 0) {
                for (int i = 0; i < downLoadItemInfos.size(); i++) {
                    if (downLoadItemInfos.get(i).getId().intValue() == record.getId()) {
                        downLoadItemInfos.set(i, record);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据下载地址和下载文件路径查找下载记录
     * <p>
     * 下载地址
     *
     * @param filePath 下载文件路径
     * @return
     */
    public DownLoadItemInfo findSigleRecord(String filePath) {
        List<DownLoadItemInfo> downloadInfoList = findRecord(filePath);
        if (downloadInfoList.isEmpty()) {
            return null;
        }
        return downloadInfoList.get(0);
    }

    /**
     * 根据id查找下载记录对象
     *
     * @param recordId
     * @return
     */
    public DownLoadItemInfo findRecordById(int recordId) {
        synchronized (DownLoadDao.class) {
            for (DownLoadItemInfo record : downLoadItemInfos) {
                if (record.getId() == recordId) {
                    return record;
                }
            }

            DownLoadItemInfo where = new DownLoadItemInfo();
            where.setId(recordId);
            List<DownLoadItemInfo> resultList = super.query(where);
            if (resultList.size() > 0) {
                return resultList.get(0);
            }
            return null;
        }

    }

    /**
     * 根据id从内存中移除下载记录
     *
     * @param id 下载id
     * @return true标示删除成功，否则false
     */
    public boolean removeRecordFromMemery(int id) {
        synchronized (DownLoadItemInfo.class) {
            for (int i = 0; i < downLoadItemInfos.size(); i++) {
                if (downLoadItemInfos.get(i).getId() == id) {
                    downLoadItemInfos.remove(i);
                    break;
                }
            }
            return true;
        }
    }

}
