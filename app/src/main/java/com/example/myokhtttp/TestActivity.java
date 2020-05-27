package com.example.myokhtttp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myokhtttp.net.download.DownFileManager;
import com.example.myokhtttp.net.download.enums.DownloadStatus;
import com.example.myokhtttp.net.download.enums.DownloadStopMode;
import com.example.myokhtttp.net.download.interfaces.IDownloadCallable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/8/12 0012 18:04
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/8/12 0012 18:04
 * @UpdateRemark: 更新说明
 * @version:
 */
public class TestActivity extends Activity {


    private TextView tv_down;
    private Button btn_down;
    private  String url = "http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv_down = findViewById(R.id.tv_down);
        btn_down = findViewById(R.id.btn_down);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downFile(url);
            }
        });
    }


    private void downFile(String url) {
        DownFileManager downFileManager = new DownFileManager();
        downFileManager.download(url);
        downFileManager.setDownCallable(new IDownloadCallable() {
            @Override
            public void onDownloadInfoAdd(int downloadId) {
            }

            @Override
            public void onDownloadInfoRemove(int downloadId) {

            }

            @Override
            public void onDownloadStatusChanged(int downloadId, DownloadStatus status) {


            }

            @Override
            public void onTotalLengthReceived(int downloadId, long totalLength) {

            }

            @Override
            public void onCurrentSizeChanged(int downloadId, double downloadpercent, long speed) {
                tv_down.setText("下载速度："+(speed / 1000 + "k/s"));
            }

            @Override
            public void onDownloadSuccess(int downloadId) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_down.setText("下载完成：");
                    }
                });

            }

            @Override
            public void onDownloadError(int downloadId, int errorCode, String errorMsg) {

            }
        });
    }
}
