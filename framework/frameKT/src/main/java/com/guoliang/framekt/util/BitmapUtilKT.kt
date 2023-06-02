package com.guoliang.framekt.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.guoliang.framekt.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/12/21 10:46
 */
object BitmapUtilKT {
    private val cachePath: String by lazy {
        val file = File(context().cacheDir.absolutePath + File.separator + "picture")
        if (!file.exists()) {
            file.mkdirs()
        }
        return@lazy file.absolutePath + File.separator
    }


    suspend fun getNetworkImageBitmap(urlPath: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            var connection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                //把传过来的路径转成URL
                val url = URL(urlPath)
                //获取连接
                connection = url.openConnection() as HttpURLConnection
                //使用GET方法访问网络
                connection.requestMethod = "GET"
                //超时时间为10秒
                connection.connectTimeout = 10000
                //获取返回码
                val code = connection.responseCode
                if (code == 200) {
                    inputStream = connection.inputStream
                    //使用工厂把网络的输入流生产Bitmap
                    bitmap = BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                //关闭读写流
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return@withContext bitmap
        }
    }

    suspend fun getNetworkCacheImageBitmap(urlPath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            var fileOutputStream: FileOutputStream? = null
            var fileInputStream: FileInputStream? = null
            val fileMd5Name = MD5Utils.md5(urlPath)
            val cacheFilePath = cachePath + fileMd5Name

            try {
                val file = File(cacheFilePath)
                if (file.exists() && file.length() > 0) {
                    fileInputStream = FileInputStream(cacheFilePath)
                    bitmap = BitmapFactory.decodeStream(fileInputStream)
                } else {
                    file.mkdirs()
                    bitmap = getNetworkImageBitmap(urlPath)
                    fileOutputStream = FileOutputStream(cacheFilePath)
                    bitmap?.let { it.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return@withContext bitmap
        }
    }

    suspend fun getNetworkCacheImageString(urlPath: String): String? {
        return withContext(Dispatchers.IO) {
            var fileOutputStream: FileOutputStream? = null
            val fileMd5Name = MD5Utils.md5(urlPath)
            val cacheFilePath = "$cachePath$fileMd5Name.png"
            var imagePath: String? = null
            try {
                val file = File(cacheFilePath)
                if (file.exists() && file.length() > 0) {
                    imagePath = cacheFilePath
                } else {
                    file.createNewFile()
                    val bitmap = getNetworkImageBitmap(urlPath)
                    fileOutputStream = FileOutputStream(cacheFilePath)
                    bitmap?.let {
                        it.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                        it.recycle()
                        imagePath = cacheFilePath
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return@withContext imagePath
        }
    }

    fun saveBitmapCachePath(bitmap: Bitmap): String {
        var fileOutputStream: FileOutputStream? = null
        var cacheFilePath = "$cachePath${System.currentTimeMillis()}.png"
        try {
            val file = File(cacheFilePath)
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            cacheFilePath = ""
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return cacheFilePath
    }


}