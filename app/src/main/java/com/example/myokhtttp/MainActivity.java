package com.example.myokhtttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myokhtttp.net.download.DownFileManager;
import com.example.myokhtttp.net.interfaces.IHttpCallBack;
import com.example.myokhtttp.net.Velloy;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    String url = "http://v.juhe.cn/movie/index";

    String fileUrl = "http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk";
    private Button btnStartNet;
    private Button btnConcurrent;
    private TextView textView;
    private ListView listView;
    //权限
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    String a = "jklsfjfasfj";
    String b = "ifasdklfjsdklafjjklsfjfasfjfioasujdfjklsfjfasfjklsfjfasf";

    //是否并发
    private boolean concurrent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartNet = (Button) findViewById(R.id.btn_start_net);


        btnConcurrent = (Button) findViewById(R.id.btn_concurrent);
        textView = (TextView) findViewById(R.id.tv_text);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnStartNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                getData();
                //                concurrent = false;
                //                PermissionsUtils.getInstance().chekPermissions(MainActivity.this, permissions, iPermissionsResult);
                //                pipei(a, b);
                AddSumFor();
                AddSumDo();
                AddSumWhile();
            }
        });

        btnConcurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));


                //                concurrent = true;
                //                PermissionsUtils.getInstance().chekPermissions(MainActivity.this, permissions, iPermissionsResult);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });


        btnStartNet.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void getData() {

        HashMap<String, String> params = new HashMap<String, String>();

        textView.setText("");
        if (concurrent) {
            for (int i = 0; i < 80; i++) {
                final int finalI = i;
                Velloy.sendJsonRequest(new Data("长城"), url, ResponceData.class, new IHttpCallBack<ResponceData>() {

                    @Override
                    public void onSuccess(ResponceData responceData) {
                        LogUtil.w(responceData.toString());
                        textView.setText(textView.getText().toString() + "\n" + "第" + finalI + "次请求:" + responceData.toString() + "\n");
                    }

                    @Override
                    public void onFaile() {

                    }
                });
            }
        } else {
            Velloy.sendJsonRequest(new Data("长城"), url, ResponceData.class, new IHttpCallBack<ResponceData>() {

                @Override
                public void onSuccess(ResponceData responceData) {
                    LogUtil.w(responceData.toString());
                    textView.setText(responceData.toString());
                }

                @Override
                public void onFaile() {

                }
            });
        }

    }

    PermissionsUtils.IPermissionsResult iPermissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            //            downFile();
            getData();
        }

        @Override
        public void forbitPermissons() {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
    }


    private void downFile() {
        DownFileManager downFileManager = new DownFileManager();
        downFileManager.download(fileUrl);
    }


    public int pipei(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        boolean x = false;

        int i = 0;
        int j = 0;
        while (i < len1 && j < len2) {
            char s = str1.charAt(i);
            char d = str2.charAt(j);
            if (s == d) {
                x = true;
                i++;
            } else {
                i = 0;
                if (x) {
                    x = false;
                    j--;
                }
            }
            j++;
            if (i == len1) {
                textView.setText("出现的位置" + (j - i));
            }
        }
        return -1;
    }

    public void AddSumFor() {
        long startTime = System.currentTimeMillis();
        int sum = 0;
        for (int i = 0; i <= 100; i++) {
            sum += i;
        }
        System.out.println("for循环 用时" + (System.currentTimeMillis() - startTime));
    }

    public void AddSumWhile() {
        long startTime = System.currentTimeMillis();
        int sum = 0;
        int i = 1;
        while (i <= 10) {
            sum = sum + i;
            i++;
        }
        System.out.println("while循环 用时" + (System.currentTimeMillis() - startTime));
    }

    public void AddSumDo() {
        long startTime = System.currentTimeMillis();
        int sum = 0;
        int i = 0;
        do {
            sum = sum = i;
        } while (i <= 100);
        System.out.println("do while 用时" + (System.currentTimeMillis() - startTime));
    }
}
