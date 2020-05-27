package com.example.myokhtttp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/31 0031 12:43
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/31 0031 12:43
 * @UpdateRemark: 更新说明
 * @version:
 */
public class MyView extends View {

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();
        requestLayout();
    }
}
