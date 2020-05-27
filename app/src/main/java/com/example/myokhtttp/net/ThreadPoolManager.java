package com.example.myokhtttp.net;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @desc: 线程管理者
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 18:26
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 18:26
 * @UpdateRemark: 更新说明
 * @version:
 */
public class ThreadPoolManager {

    //请求队列  容量无限大   阻塞式队列
    private LinkedBlockingQueue<Future<?>> taskQueue = new LinkedBlockingQueue<>();

    //线程池
    private ThreadPoolExecutor threadPoolExecutor;

    private static final ThreadPoolManager instance = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return instance;
    }


    private ThreadPoolManager() {
        threadPoolExecutor = new ThreadPoolExecutor(getNumCores(), 20, 10,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(4), rejectedExecutionHandler);
        //开启线程
        threadPoolExecutor.execute(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                //当队列里面没有请求时  一直阻塞
                FutureTask futureTask = null;

                try {
                    //阻塞式函数
                    futureTask = (FutureTask) taskQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //执行请求
                if (null != futureTask) {
                    threadPoolExecutor.execute(futureTask);
                }
            }
        }
    };

    /**
     * @param :
     * @method 添加请求
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/26 0026 18:29
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/26 0026 18:29
     * @UpdateRemark: 更新说明
     */
    public <T> void exectu(FutureTask<T> futureTask) throws InterruptedException {
        if (null != futureTask) {
            taskQueue.put(futureTask);
        }
    }

    /**
     * @method 拒绝策略
     * @dec : 方法的作用
     * @author :xuwh
     * @param :
     * @date : 2019/7/26 0026 18:49
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/26 0026 18:49
     * @UpdateRemark: 更新说明
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            try {
                taskQueue.put(new FutureTask(runnable, null) {
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @return : int 类型  cpu核心数
     * @method 获取cpu核心数
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/27 0027 17:13
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/27 0027 17:13
     * @UpdateRemark: 更新说明
     */
    private int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            return 1;
        }
    }

    //移除
    public <T> boolean removeTask(FutureTask futureTask) {
        boolean result = false;

        //阻塞队列中是否含有此线程
        if (taskQueue.contains(futureTask)) {
            result = taskQueue.remove(futureTask);
        } else {
            result = threadPoolExecutor.remove(futureTask);
        }

        return result;

    }
}
