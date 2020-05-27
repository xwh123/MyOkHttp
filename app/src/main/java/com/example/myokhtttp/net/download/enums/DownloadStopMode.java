package com.example.myokhtttp.net.download.enums;

/**
 * @desc: 下载模式
 * @className:DownloadStopMode
 * @author:xuwh
 * @date:2019/7/28 0028 19:03
 * @version 1.0
 */
public enum DownloadStopMode
{
    /**
     * 后台根据下载优先级调度自动停止下载任务
     */
    auto(0),

    /**
     * 手动停止下载任务
     */
    hand(1);
    DownloadStopMode(Integer value)
    {
        this.value = value;
    }

    /**
     * 值
     */
    private Integer value;

    public Integer getValue()
    {
        return value;
    }

    public void setValue(Integer value)
    {
        this.value = value;
    }

    public static DownloadStopMode getInstance(int value)
    {
        for (DownloadStopMode mode : DownloadStopMode.values())
        {
            if (mode.getValue() == value)
            {
                return mode;
            }
        }
        return DownloadStopMode.auto;
    }
}
