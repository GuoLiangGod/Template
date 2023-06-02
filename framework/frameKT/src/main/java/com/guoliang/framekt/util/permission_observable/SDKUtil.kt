package com.guoliang.framekt.util.permission_observable

import android.content.Context

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime:  2021/3/2 13:51
 */
object SDKUtil {
    /**
     * 判断 用户是否安装微信客户端
     */
    fun isWeixinAvilible(context: Context): Boolean {
        val packageManager = context.packageManager// 获取packagemanager
        val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == "com.tencent.mm") {
                    return true
                }
            }
        }
        return false
    }
}