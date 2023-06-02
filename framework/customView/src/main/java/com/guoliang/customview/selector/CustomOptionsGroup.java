package com.guoliang.customview.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/9/21 9:37
 */
public class CustomOptionsGroup extends LinearLayout implements View.OnClickListener {
    private List<View> childList = new ArrayList<>();
    private Object tag;
    private Integer selectId;

    public CustomOptionsGroup(Context context) {
        this(context, null);
    }

    public CustomOptionsGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomOptionsGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnClickListener onClickListener;

    public void setOnChildClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childList.clear();
        setChildAtListener(this);
    }

    private void setChildAtListener(View child) {
        if (child instanceof CustomOptionsLayout||child instanceof CustomOptionsButton) {
            child.setOnClickListener(this);
            if (tag!=null) {
                child.setSelected(child.getTag()==tag);
            }
            if (selectId!=null) {
                child.setSelected(child.getId() == selectId);
            }
            childList.add(child);
        } else if (child instanceof ViewGroup) {
            ViewGroup view = (ViewGroup) child;
            for (int i = 0; i < view.getChildCount(); i++) {
                setChildAtListener(view.getChildAt(i));
            }
        }
    }

    public void setSelectViewId(int id) {
        this.selectId = id;
        for (int i = 0; i < childList.size(); i++) {
            View child = childList.get(i);
            if (child.getId() == id) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
    }

    public void setSelectViewTag(Object tag) {
        this.tag = tag;
        for (int i = 0; i < childList.size(); i++) {
            View child = childList.get(i);
            if (child.getTag().equals(tag)) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
    }

    public int getSelectViewId() {
        int id = 0;
        for (int i = 0; i < childList.size(); i++) {
            View child = childList.get(i);
            if (child.isSelected()) {
                id = child.getId();
                break;
            }
        }
        return id;
    }

    public View getSelectView() {
        View view = null;
        for (int i = 0; i < childList.size(); i++) {
            View child = childList.get(i);
            if (child.isSelected()) {
                view = child;
                break;
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        tag = v.getTag();
        for (int i = 0; i < childList.size(); i++) {
            View child = childList.get(i);
            if (child != v) {
                child.setSelected(false);
            } else {
                child.setSelected(true);
            }
        }
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }
}
