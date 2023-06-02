package com.guoliang.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;


public class CustomSeekBar extends androidx.appcompat.widget.AppCompatImageView {

    private final Bitmap slidingBackground;
    private final Rect mSlidingRect;
    private final RectF mSlidingXYRect;
    private float conversion;
    private int max;
    private OnChangeListener onChangeListener;


    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 加载自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
        slidingBackground = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.CustomSeekBar_sliding_drawable, 0));
        mSlidingRect = new Rect(0, 0, slidingBackground.getWidth(), slidingBackground.getHeight());
        mSlidingXYRect = new RectF(0, 0, slidingBackground.getWidth(), slidingBackground.getHeight());
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(slidingBackground, mSlidingRect, mSlidingXYRect, null);
    }

    private boolean inTouch = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int sliding = slidingBackground.getWidth() / 2;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                inTouch = true;
                if (x < sliding) {
                    mSlidingXYRect.set(0, 0, slidingBackground.getWidth(), slidingBackground.getHeight());
                } else if (x > getWidth() - sliding) {
                    mSlidingXYRect.set(getWidth() - slidingBackground.getWidth(), 0, getWidth(), slidingBackground.getHeight());
                } else {
                    mSlidingXYRect.set(x - sliding, 0, x + sliding, slidingBackground.getHeight());
                }
                if (onChangeListener != null) {
                    onChangeListener.onProgressSliding((int) (mSlidingXYRect.left / conversion));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (onChangeListener != null) {
                    onChangeListener.onProgressChanged((int) (mSlidingXYRect.left / conversion));
                }
                inTouch = false;
                break;
        }
        return true;
    }

    public void setMax(int max) {
        this.max = max;
        post(() -> {
            float total = getWidth() - slidingBackground.getWidth() * 1f;
            conversion = total / max;
        });
    }

    public int getMax() {
        return max;
    }

    public void setProgress(int progress) {
        if (inTouch) return;
        mSlidingXYRect.left = progress * conversion;
        mSlidingXYRect.right = mSlidingXYRect.left + slidingBackground.getWidth();
        invalidate();
    }

    public int getProgress() {
        return (int) (mSlidingXYRect.left / conversion);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public interface OnChangeListener {
        void onProgressSliding(int progress);

        void onProgressChanged(int progress);
    }
}
