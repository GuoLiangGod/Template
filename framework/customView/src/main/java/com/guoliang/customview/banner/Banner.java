package com.guoliang.customview.banner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.guoliang.customview.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/11/18 10:09
 */
public class Banner extends ViewPager {
    private final List<Integer> imageList=new ArrayList<>();
    private final Context context;
    private final Handler handler=new Handler();
    private boolean isSwitch=true;
    private final int carouselTime=3000;
    private boolean isCanScroll = true;
    private OnClickBannerListener onClickBannerListener;
    private final Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if (isSwitch) {
                setCurrentItem(getCurrentItem() + 1);
            }
            handler.postDelayed(this,carouselTime);
        }
    };
    private MyAdapter myAdapter;
    private IndicatorView indicator;
    private int itemCount= Integer.MAX_VALUE;
    private float interval = 0;

    public Banner(@NonNull Context context) {
        this(context,null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        myAdapter = new MyAdapter();
        setAdapter(myAdapter);
        setOffscreenPageLimit(3);
        // 加载自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        interval = typedArray.getDimension(R.styleable.Banner_interval, 0);
    }
    public void setImageList(List<Integer> imageList){
        setViewPagerScrollSpeed(context,this);
        this.imageList.addAll(imageList);
        myAdapter.notifyDataSetChanged();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isSwitch=false;
                        handler.removeCallbacks(runnable);
                        break;
                    case MotionEvent.ACTION_UP:
                        isSwitch=true;
                        handler.postDelayed(runnable,carouselTime);
                        break;
                }
                return false;
            }
        });
        handler.postDelayed(runnable,carouselTime);
        setCurrentItem(Integer.MAX_VALUE/2);
    }

    public void setForbidImageList(List<Integer> imageList){
        itemCount = imageList.size();
        this.imageList.addAll(imageList);
        myAdapter.notifyDataSetChanged();
        setCurrentItem(0);
    }

    public void setOnClickBannerListener(OnClickBannerListener onClickBannerListener) {
        this.onClickBannerListener = onClickBannerListener;
    }

    private void setViewPagerScrollSpeed(Context context, ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(context);
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIndicator( IndicatorView indicator) {
        this.indicator = indicator;
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        int realPosition = toRealPosition(position);
        if (indicator != null) {
            indicator.onPageScrolled(realPosition, offset, offsetPixels);
        }
    }

    /**
     * 获取存在下标
     * @param position
     * @return
     */
    private int toRealPosition(int position) {
        if (imageList.size()>0) {
            return position % imageList.size();
        }else {
            return 0;
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return itemCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (imageList.size()==0) return null;
            int newPosition = position % imageList.size();
            ImageView imageView = new ImageView(context);
            imageView.setPadding((int)interval,0,(int)interval,0);
//            imageView.setFitsSystemWindows(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageDrawable(ContextCompat.getDrawable(context,imageList.get(newPosition)));
            imageView.setOnClickListener(v -> {
                if (onClickBannerListener!=null){
                    onClickBannerListener.onClickIndex(newPosition);
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private static class FixedSpeedScroller extends Scroller {

        private final int mDuration = 1000;//这里是定义切换的时长

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

    }
    public interface OnClickBannerListener{
        void onClickIndex(int index);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            int currentItem = ((ViewPager) v).getCurrentItem();
            int countItem = ((ViewPager) v).getAdapter().getCount();
            return (currentItem != (countItem - 1) || dx >= 0)
                    && (currentItem != 0 || dx <= 0);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return isCanScroll && super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return isCanScroll && super.onInterceptTouchEvent(arg0);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        WindowInsets result = super.dispatchApplyWindowInsets(insets);
        if (!insets.isConsumed()) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++)
                result = getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        return result;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ViewCompat.requestApplyInsets(child);
    }
}
