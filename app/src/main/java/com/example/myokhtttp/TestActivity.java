package com.example.myokhtttp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

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

    private ListView listView;

    private List<String> data;
    private ListAdapter listAdapter;
    private Runnable runable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = findViewById(R.id.list_view);

        data = new ArrayList<String>();
        listAdapter = new ListAdapter(this, data);

        listView.setAdapter(listAdapter);
        runable = new runable();
        new Thread(runable).start();
    }


    class runable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                data.add("第" + i + "行数据");
            }

            Message message = Message.obtain();
            message.what = 1;
            handler.sendMessage(message);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runable);
    }
}
