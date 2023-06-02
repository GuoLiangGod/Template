package com.guoliang.customview

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/12/25 9:33
 */
class AppBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    val toolbar: Toolbar
    val tvTitle: TextView
    val tvLeft: TextView
    val tvRight: TextView
    val ivLeft: ImageView
    val ivRight: ImageView

    init {
        View.inflate(context, R.layout.view_app_bar, this)
        toolbar = findViewById(R.id.toolbar)
        tvTitle = findViewById(R.id.tv_title)
        tvLeft = findViewById(R.id.tv_left)
        tvRight = findViewById(R.id.tv_right)
        ivLeft = findViewById(R.id.iv_left)
        ivRight = findViewById(R.id.iv_right)
        // 加载自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppBarView)
        val testAppTitle = typedArray.getString(R.styleable.AppBarView_app_bar_title)
        val testLeftTitle = typedArray.getString(R.styleable.AppBarView_app_bar_left_title)
        val testRightTitle = typedArray.getString(R.styleable.AppBarView_app_bar_right_title)

        val appDrawable = typedArray.getDrawable(R.styleable.AppBarView_app_bar_background)

        val appLeftDrawable = typedArray.getDrawable(R.styleable.AppBarView_app_bar_left_image)
        val appRightDrawable = typedArray.getDrawable(R.styleable.AppBarView_app_bar_right_image)
        val appLeftTextDrawable = typedArray.getDrawable(R.styleable.AppBarView_app_bar_left_text_drawable)
        val appRightTextDrawable = typedArray.getDrawable(R.styleable.AppBarView_app_bar_right_text_drawable)
        val appTextSize = typedArray.getDimension(R.styleable.AppBarView_app_bar_text_size, tvTitle.textSize)
        val appLeftTextSize = typedArray.getDimension(R.styleable.AppBarView_app_bar_left_text_size, tvLeft.textSize)
        val appRightTextSize = typedArray.getDimension(R.styleable.AppBarView_app_bar_right_text_size, tvRight.textSize)
        val appTextColor = typedArray.getColor(R.styleable.AppBarView_app_bar_text_color, Color.BLACK)
        val appLeftTextColor = typedArray.getColor(R.styleable.AppBarView_app_bar_left_text_color, Color.BLACK)
        val appRightTextColor = typedArray.getColor(R.styleable.AppBarView_app_bar_right_text_color, Color.BLACK)
        val appBarPaddingLeft = typedArray.getDimension(R.styleable.AppBarView_app_bar_left_padding, ivLeft.paddingLeft.toFloat())
        val appBarPaddingRight = typedArray.getDimension(R.styleable.AppBarView_app_bar_right_padding, ivRight.paddingLeft.toFloat())
        tvTitle.text = testAppTitle
        tvTitle.setTextColor(appTextColor)
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, appTextSize.toFloat())

        tvLeft.text = testLeftTitle
        tvLeft.setTextColor(appLeftTextColor)
        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, appLeftTextSize)

        tvRight.text = testRightTitle
        tvRight.setTextColor(appRightTextColor)
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, appRightTextSize)

        appLeftDrawable?.let {
            ivLeft.setImageDrawable(it)
        }
        appRightDrawable?.let {
            ivRight.visibility = View.VISIBLE
            ivRight.setImageDrawable(it)
        }
        appLeftTextDrawable?.let {
            tvLeft.background = it
        }
        appRightTextDrawable?.let {
            tvRight.background = it
        }
        appDrawable?.let {
            background = it
        }
        tvLeft.setPadding(appBarPaddingLeft.toInt(),0,appBarPaddingLeft.toInt(),0)
        ivLeft.setPadding(appBarPaddingLeft.toInt(),0,appBarPaddingLeft.toInt(),0)
        tvRight.setPadding(appBarPaddingRight.toInt(),0,appBarPaddingRight.toInt(),0)
        ivRight.setPadding(appBarPaddingRight.toInt(),0,appBarPaddingRight.toInt(),0)
        ivLeft.setOnClickListener {
            (context as Activity).onBackPressed()
        }
        typedArray.recycle()
    }
}