package com.example.myokhtttp;

import android.os.AsyncTask;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/30 0030 21:46
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/30 0030 21:46
 * @UpdateRemark: 更新说明
 * @version:
 */
public class MyAsyncTask extends AsyncTask<Integer,Integer,String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... integers) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
