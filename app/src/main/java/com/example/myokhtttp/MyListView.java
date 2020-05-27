package com.example.myokhtttp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/8/12 0012 18:39
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/8/12 0012 18:39
 * @UpdateRemark: 更新说明
 * @version:
 */
public class MyListView extends ListView {
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
