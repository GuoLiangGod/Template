package com.guoliang.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/12/17 9:58
 */
public class ControlSlideViewPager extends ViewPager {
    private boolean isCanScroll = false;
    public ControlSlideViewPager(@NonNull Context context) {
        super(context);
    }

    public ControlSlideViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public final void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.isCanScroll && super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.isCanScroll && super.onTouchEvent(ev);
    }

}
