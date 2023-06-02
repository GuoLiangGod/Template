package com.guoliang.customview.scrollview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Created by tonyan on 2020/5/25.
 */

public class MainScrollView extends ScrollView{

    private TextView recentUseTitle;

    private RelativeLayout topTitleLayout;

    private LinearLayout backToTopLayout;

    public void stopScroll() {
        // do nothing
    }

    public void scrollToTop() {
        smoothScrollTo(0, 0);
    }

    public MainScrollView(Context context) {
        super(context);
        setOnScrollChangeListener();
    }

    public MainScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollChangeListener();
    }

    public MainScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollChangeListener();
    }

    private void setOnScrollChangeListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setOnScrollChangeListener((view, i, i1, i2, i3) -> {
                if (recentUseTitle != null) {
                    if (ViewCheckTool.isScrollToTop(recentUseTitle)) {
                        recentUseTitle.setVisibility(View.INVISIBLE);
                        topTitleLayout.setVisibility(View.VISIBLE);
                        stopScroll();
                    } else {
                        recentUseTitle.setVisibility(View.VISIBLE);
                        topTitleLayout.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            getViewTreeObserver().addOnScrollChangedListener(() -> {
                if (recentUseTitle != null) {
                    if (ViewCheckTool.isScrollToTop(recentUseTitle)) {
                        recentUseTitle.setVisibility(View.INVISIBLE);
                        topTitleLayout.setVisibility(View.VISIBLE);
                        stopScroll();
                    } else {
                        recentUseTitle.setVisibility(View.VISIBLE);
                        topTitleLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}
