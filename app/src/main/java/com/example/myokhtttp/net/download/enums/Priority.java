package com.example.myokhtttp.net.download.enums;

/**
 * @desc: 下载优先级
 * @className:Priority
 * @author:xuwh
 * @date:2019/7/28 0028 17:13
 * @version 1.0
 */
public enum Priority
{
    /**
     * 手动下载的优先级
     */
    low(0),

    /**
     * 主动推送资源的手动恢复的优先级
     */
    middle(1),

    /**
     * 主动推送资源的优先级
     */
    high(2);
    Priority(int value)
    {
        this.value = value;
    }

    private int value;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public static Priority getInstance(int value)
    {
        for (Priority priority : Priority.values())
        {
            if (priority.getValue() == value)
            {
                return priority;
            }
        }
        return Priority.middle;
    }
}
