package com.example.myokhtttp.net;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.example.myokhtttp.net.interfaces.IHttpCallBack;
import com.example.myokhtttp.net.interfaces.IHttpListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @desc: json 回调
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 19:26
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 19:26
 * @UpdateRemark: 更新说明
 * @version:
 */
public class JsonHttpListener<M> implements IHttpListener {

    private IHttpCallBack<M> iHttpCallBack;

    private Class<M> responseClass;

    private Handler handler = new Handler(Looper.getMainLooper());

    public JsonHttpListener(IHttpCallBack<M> jsonCallBack, Class<M> responseClass) {
        this.iHttpCallBack = jsonCallBack;
        this.responseClass = responseClass;
    }

    @Override
    public void onSuccess(InputStream inputStream, long dataLength) {
        String jsonContent =getContent(inputStream);

        //把内容解析成封装对象
        final M parseObject = JSON.parseObject(jsonContent, responseClass);

        //线程切换
        handler.post(new Runnable() {
            @Override
            public void run() {
                iHttpCallBack.onSuccess(parseObject);
            }
        });
    }

    @Override
    public void onFaile() {
        //线程切换
        handler.post(new Runnable() {
            @Override
            public void run() {
                iHttpCallBack.onFaile();
            }
        });
    }

    @Override
    public void addHttpHeader(Map<String, String> headerMap) {

    }

    /**
     * @method  字节输入流转字符串
     * @dec : 方法的作用
     * @author :xuwh
     * @param  :
     * @date : 2019/7/26 0026 20:25
     * @UpdateUser:     更新者
     * @UpdateDate:     2019/7/26 0026 20:25
     * @UpdateRemark:   更新说明
     */
    private String getContent(InputStream inputStream) {
        String content = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            try {
                while (null != (line = reader.readLine())) {
                    stringBuilder.append(line + "\n");
                }
            }catch (IOException e){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        iHttpCallBack.onFaile();
                    }
                });
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iHttpCallBack.onFaile();
                        }
                    });
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return content;
    }
}
