package com.guoliang.framekt.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.telephony.TelephonyManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime:  2021/2/27 16:34
 */
object AndroidUtil {
    /**
     * 读取application 节点  meta-data 信息
     */
    fun readMetaDataFromApplication(context: Context, key: String):String {
        try {
            val appInfo: ApplicationInfo = context.packageManager
                    .getApplicationInfo(
                            context.packageName,
                            PackageManager.GET_META_DATA
                    )
            return appInfo.metaData.getString(key) ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getJsonFromAssets(name: String): String? {
        val sb = StringBuffer()
        try {
            val inputStream = javaClass.getResourceAsStream(name)
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    /** 根据路径获取Bitmap图片
     * @param context
     * @param path
     * @return
     */
    fun getAssetsBitmap(context: Context, path: String): Bitmap? {
        val am = context.assets
        var inputStream: InputStream? = null
        try {
            inputStream = am.open(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(inputStream)
    }

    fun hasSimCard(context: Context): Boolean {
        val telMgr =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simState = telMgr.simState
        var result = true
        when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> result = false // 没有SIM卡
            TelephonyManager.SIM_STATE_UNKNOWN -> result = false
        }
        return result
    }
}