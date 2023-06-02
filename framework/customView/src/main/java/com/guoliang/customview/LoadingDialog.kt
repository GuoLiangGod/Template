package com.guoliang.customview

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/7/3 10:49
 */
class LoadingDialog(val activity: Activity,var title:String?=null,var cancelTest:String?=null) : Dialog(activity,R.style.loading_dialog) {
    private val sResources = Resources.getSystem()
    private lateinit var txEmpty:TextView
    private lateinit var txCancel:TextView
    var onClickCancel:(()->Unit)?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = View.inflate(activity, R.layout.dialog_loading_layout, null)
        txEmpty = inflate.findViewById<TextView>(R.id.tx_empty)
        txCancel = inflate.findViewById<TextView>(R.id.tv_cancel)
        title?.let { txEmpty.text=it }
        cancelTest?.let { txCancel.text=it }
        txCancel.setOnClickListener {
            dismiss()
            onClickCancel?.invoke()
        }
        setContentView(inflate)
        setCancelable(false)
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK&&event.repeatCount ==0){
                if (cancelTest!=null){
                    txCancel.visibility = View.VISIBLE
                }
                return@setOnKeyListener true
            }else{
                return@setOnKeyListener false
            }
        }
    }

    fun setLoadingTitle(title: String?){
        this.title = title
        if (this::txEmpty.isInitialized) {
            this.title?.let { txEmpty.text = it }
        }
    }
    fun setCancelText(cancelTest:String){
        this.cancelTest = cancelTest
        if (this::txEmpty.isInitialized) {
            this.cancelTest?.let { txCancel.text = it }
        }
    }

    override fun show() {
        try {
            if (!isShowing) {
                super.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            if (isShowing) {
                super.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setIsShow(it: Boolean) {
        if (it){
            show()
        }else{
            dismiss()
        }
    }
}