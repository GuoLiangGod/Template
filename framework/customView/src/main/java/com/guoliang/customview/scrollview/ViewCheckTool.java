package com.guoliang.customview.scrollview;

import android.view.View;

/**
 * Created by tonyan on 2020/5/25.
 */

public class ViewCheckTool {

    public static boolean isScrollToTop(View view){
        int[] location = new int[2];
        view.getLocationInWindow(location); //获取在当前窗口内的绝回对坐答标
        view.getLocationOnScreen(location);
//        Log.e("check_y","title_y:" + location[1]);
        return location[1] <= 80;
    }
}
