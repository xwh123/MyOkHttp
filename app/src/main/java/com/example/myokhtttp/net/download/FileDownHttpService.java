package com.example.myokhtttp.net.download;

import android.os.Environment;

import com.example.myokhtttp.LogUtil;
import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @desc: 文件下载
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/27 0027 18:20
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/27 0027 18:20
 * @UpdateRemark: 更新说明
 * @version:
 */
public class FileDownHttpService implements IHttpService {

    private String url;

    private HttpURLConnection urlConnection;

    /**
     * 增加方法
     */
    private AtomicBoolean pause=new AtomicBoolean(false);

    /**
     * 即将添加到请求头的信息
     */
    private Map<String, String> headerMap = Collections.synchronizedMap(new HashMap<String, String>());

    //请求处理的接口
    private IHttpListener httpListener;
    private byte[] requestData;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void excute() {
        //        URL url = new URL("https://57e347d9cca4cbb2cb0f833e86d7ca1a.dd.cdntips.com/imtt.dd.qq.com/16891/34D3EECDE5B27CFBE996173932357FE9.apk?");
        URL url = null;
        try {
            url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            addHead();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();//连接

            //字节流写入数据
            InputStream inputStream = null;
            LogUtil.w("返回码" + urlConnection.getResponseCode());
            if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()||HttpURLConnection.HTTP_PARTIAL==urlConnection.getResponseCode()) {
                inputStream = urlConnection.getInputStream();
                httpListener.onSuccess(inputStream, urlConnection.getContentLength());
            } else {
                httpListener.onFaile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            httpListener.onFaile();
        }
    }

    @Override
    public void setRequest(byte[] bytes) {
        this.requestData = bytes;
    }

    @Override
    public void setHttpcallback(IHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    @Override
    public void pause() {
        pause.compareAndSet(false,true);
    }

    /**
     * @method 添加断点下载的请求头
     * @dec : 方法的作用
     * @author :xuwh
     * @date : 2019/7/28 0028 15:44
     * @UpdateUser: 更新者
     * @UpdateDate: 2019/7/28 0028 15:44
     * @UpdateRemark: 更新说明
     */
    private void addHead() {
        Iterator iterator = headerMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = headerMap.get(key);
            urlConnection.setRequestProperty(key,value);
        }

    }

    @Override
    public Map<String, String> getHttpHeadMap() {
        return headerMap;
    }

    @Override
    public boolean isCancle() {
        return false;
    }

    @Override
    public boolean cancle() {
        return false;
    }

    @Override
    public boolean isPause() {
        return pause.get();
    }
}
