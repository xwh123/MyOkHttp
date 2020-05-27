package com.example.myokhtttp.net.download.enums;

/**
 * @desc: 下载状态
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 19:24
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 19:24
 * @UpdateRemark: 更新说明
 * @version:
 */
public enum DownloadStatus {

    waitting(0),

    starting(1),

    downloading(2),
    pause(3),
    finish(4),
    failed(5);

    private int value;

    private DownloadStatus(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
