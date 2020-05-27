package com.example.myokhtttp.net;

import com.example.myokhtttp.net.interfaces.IHttpListener;
import com.example.myokhtttp.net.interfaces.IHttpService;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @desc: json 请求
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 19:25
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 19:25
 * @UpdateRemark: 更新说明
 * @version:
 */
public class JsonHttpService implements IHttpService {

    private String url;
    private IHttpListener iHttpListener;
    private byte[] requestData;

    private HttpURLConnection urlConnection;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void excute() {
        httpUrlconnPost();
    }

    @Override
    public void setRequest(byte[] bytes) {
        this.requestData = bytes;
    }

    @Override
    public void setHttpcallback(IHttpListener httpListener) {
        this.iHttpListener = httpListener;
    }

    @Override
    public void pause() {

    }

    @Override
    public Map<String, String> getHttpHeadMap() {
        return null;
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
        return false;
    }

    private void httpUrlconnPost() {
        URL url = null;

        try {
            url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);//连接超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            urlConnection.setInstanceFollowRedirects(true);//是成员函数  仅作用于当前函数  设置是否重定向
            urlConnection.setReadTimeout(3000);//设置响应的超时时间
            urlConnection.setDoInput(true);//设置此连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置此连接是否可以输出数据
            urlConnection.setRequestMethod("POST");//设置请求方式
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置请求格式及编码
            urlConnection.connect();//连接
            //使用字节流发送数据
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);//缓冲字节流包装字节流
            bufferedOutputStream.write(requestData);//把字节数组的数据写入缓冲区、
            bufferedOutputStream.flush();//刷新缓冲区，发送数据
            outputStream.close();
            bufferedOutputStream.close();

            //字节流写入数据
            InputStream inputStream = null;
            if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
                inputStream = urlConnection.getInputStream();
                iHttpListener.onSuccess(inputStream,urlConnection.getContentLength());
            } else {
                iHttpListener.onFaile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            iHttpListener.onFaile();
        }finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }

    }
}
