package com.guoliang.framekt.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import androidx.core.net.toFile
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/5/26 18:52
 */
object FileUtil {
    private const val TAG = "FileUtil"

    /**
     * 文件删除
     * @param filePath 文件地址
     * @return 是否删除成功
     */
    fun fileDelete(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * 对文件重命名
     * @param filePath 文件地址
     * @param reName 新命名
     * @return 是否成功
     */
    fun fileRename(filePath: String, reName: String): Boolean {
        val file = File(filePath)
        val path =
                filePath.substring(0, filePath.lastIndexOf("/") + 1) + reName + filePath.substring(
                    filePath.lastIndexOf("."),
                    filePath.length
                )
        val newFile = File(path)
        return file.renameTo(newFile)
    }

    /**
     * 对文件重命名
     * @param filePath 文件地址
     * @param reName 新命名
     * @return 是否成功
     */
    fun fileRenamePath(filePath: String, reNamePath: String): Boolean {
        val file = File(filePath)
        val newFile = File(reNamePath)
        return file.renameTo(newFile)
    }

    fun copyFile(oldPath: String, block: ((String) -> Unit)? = null): Boolean {
        try {
            var byTeam = 0
            var bantered = 0
            val oldFile = File(oldPath)
            val newPath = getCopyNameFromOriginalTest(oldPath)
            //文件存在时
            if (oldFile.exists()) {
                //读入原文件
                val inStream: InputStream = FileInputStream(oldPath)
                val fs = FileOutputStream(newPath)
                val buffer = ByteArray(1444)
                while (inStream.read(buffer).also { bantered = it } != -1) {
                    byTeam += bantered //字节数 文件大小
                    println(byTeam)
                    fs.write(buffer, 0, bantered)
                }
                inStream.close()
            }
            block?.invoke(newPath)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(TAG, e.message)
            return false
        }
    }

    /**
     * @Description 得到文件副本名称，可供粘贴及多选重命名方法使用
     * 命名规则为：普通文件后加“ 1”，若文件末尾已有“ 数字”，则数字递增。
     * 比如，有个文件叫“我.jpg”，使用本方法后得到了“我 1.jpg”，再次使用本方法后得到“我 2.jpg”
     * @param originalName 原本的名字，XXX.xx 或者完整路径 xx/xx/XXX.xx ， 也可以没有后缀.xx
     * @return 副本名称
     */
    fun getCopyNameFromOriginal(originalName: String?): String? {
        //1.判断阈值
        if (originalName == null || originalName.isEmpty()) {
            return null
        }
        var copyName: String? = null
        //2.得到文件名和后缀名

        val nameAndExt: List<String> = originalName.split(".")
        val sb = StringBuilder()
        nameAndExt.forEachIndexed { index, s ->
            if (index != nameAndExt.size - 1) {
                sb.append(s)
            }
        }
        val fileName = sb.toString()
        val fileExt = "." + nameAndExt[nameAndExt.size - 1]
        //3.判断文件名是否包含我们定义副本规范的标记字符（空格）
        if (fileName.contains(" ")) { //如果文件名包涵空格，进行判断是否已经为副本名称
            //4-1.得到end
            var array = fileName.split(" ".toRegex()).toTypedArray()
            var end = array[array.size - 1] //得到标记字符后面的值
            //4-2.确保end得到的是最后面的值（防止出现类似路径中的目录也有标记字符的情况，如："mnt/sda/wo de/zhao pian/我的 照片 1.png"）
            while (end.contains(" ")) {
                array = fileName.split(" ".toRegex()).toTypedArray()
                end = array[array.size - 1]
            }
            //5.判断标记字符后的字符串是否复合规范（是否是数字）
            val isDigit = end.matches(Regex("[0-9]+")) //用正则表达式判断是否是正整数
            if (isDigit) {
                try {
                    val index = end.toInt() + 1 //递增副本记数
                    val position = fileName.lastIndexOf(" ") //得到最后的空格的位置，用于截取前面的字符串
                    if (position != -1) {
                        //6-1.构造新的副本名（数字递增）
                        copyName = fileName.substring(0, position + 1) + index.toString()
                    }
                } catch (e: java.lang.Exception) { //转化成整形错误
                    e.printStackTrace()
                    return null
                }
            } else { //如果空格后不是纯数字，即不为我们定义副本的规范
                //6-2.构造新的副本名（数字初始为1）
                copyName = "$fileName 1"
            }
        } else { //如果没有，则变为副本名称格式
            //6-3.构造新的副本名（数字初始为1）
            copyName = "$fileName 1"
        }
        LogUtil.e(TAG, "new copy name is $copyName$fileExt")
        //6.返回副本名+后缀名
        return copyName + fileExt
    }

    /**
     * 重命名或复制规则
     * originalName:源文件路径
     * xxx(1)
     * xxx(1)(1)
     * xxx(1)(2)
     * xxx(1)(2)(1)
     * xxx(1)(2)(2)
     * xxx(1)(2)(2)(1)
     */
    private fun getCopyNameFromOriginalTest(originalName: String): String {
        val oldFile = File(originalName)
        val oldFileName = oldFile.name
        val nameAndExt: List<String> = oldFileName.split(".")
        val sb = StringBuilder()
        //防止名称有.符合
        nameAndExt.forEachIndexed { index, s ->
            if (index != nameAndExt.size - 1) {
                sb.append(s)
            }
        }
        var fileName = sb.toString()
        val fileExt = "." + nameAndExt[nameAndExt.size - 1]
        var newTimeFileName = "$fileName(1)$fileExt"
        fileName=fileName.replace("(", "\\(")
        fileName=fileName.replace(")", "\\)")
        val oldRegex="$fileName[(][0-9]+[)]$fileExt"
        val pattern = Pattern.compile(oldRegex)
        val parentFile = oldFile.parentFile
        var number = 1
        parentFile!!.listFiles()?.forEach { it ->
            val matcher = pattern.matcher(it.name)
            if (matcher.find() && it.length() == oldFile.length()) {
                val newNumber = it.name.substring(
                    it.name.lastIndexOf("(") + 1, it.name.lastIndexOf(
                        ")"
                    )
                ).toInt()
                number = if (newNumber >= number) newNumber + 1 else number
                newTimeFileName = it.name.substring(0, it.name.lastIndexOf("(") + 1) + number + it.name.substring(
                    it.name.lastIndexOf(
                        ")"
                    ), it.name.length
                )
            }
        }
        return parentFile.absolutePath + "/" + newTimeFileName
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean 是否删除
     */
    fun copyFile(oldPath: String, newPath: String, isRetain: Boolean = false): Boolean {
        return try {
            var byTeam = 0
            var bantered = 0
            val oldFile = File(oldPath)
            //文件存在时
            if (oldFile.exists()) {
                //读入原文件
                val inStream: InputStream = FileInputStream(oldPath)
                val fs = FileOutputStream(newPath)
                val buffer = ByteArray(1444)
                while (inStream.read(buffer).also { bantered = it } != -1) {
                    byTeam += bantered //字节数 文件大小
                    println(byTeam)
                    fs.write(buffer, 0, bantered)
                }
                inStream.close()
                if (isRetain) {
                    oldFile.delete()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(TAG, e.message)
            false
        }
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean 是否成功
     */
    fun copyFolder(oldPath: String, newPath: String): Boolean {
        return try {
            val newFile = File(newPath)
            //如果文件夹不存在 则建立新文件夹
            if (!newFile.mkdirs()) {
                val mkdirs = newFile.mkdirs()
            }
            val a = File(oldPath)
            val file = a.list()
            var temp: File? = null
            for (i in 0 until (file?.size ?: 0)) {
                temp = if (oldPath.endsWith(File.separator)) {
                    File(oldPath + file!![i])
                } else {
                    File(oldPath + File.separator + file!![i])
                }
                if (temp.isFile) {
                    val input = FileInputStream(temp)
                    val output = FileOutputStream(
                        newPath + "/" +
                                temp.name.toString()
                    )
                    val b = ByteArray(1024 * 5)
                    var len: Int
                    while (input.read(b).also { len = it } != -1) {
                        output.write(b, 0, len)
                    }
                    output.flush()
                    output.close()
                    input.close()
                }
                if (temp.isDirectory) { //如果是子文件夹
                    copyFolder(
                        oldPath + "/" + file[i],
                        newPath + "/" + file[i]
                    )
                }
            }
            true
        } catch (e: Exception) {
            LogUtil.e(TAG, e.message)
            false
        }
    }

    fun unZip(zipFile: File, outDir: String?): File {
        var name = zipFile.name
        name = name.replace(".zip".toRegex(), "")
        val outFileDir = File(outDir, name)
        if (!outFileDir.exists()) {
            outFileDir.mkdirs()
        }
        val zip = ZipFile(zipFile)
        val enumeration: Enumeration<*> = zip.entries()
        while (enumeration.hasMoreElements()) {
            val entry: ZipEntry = enumeration.nextElement() as ZipEntry
            val zipEntryName: String = entry.getName()
            val `in`: InputStream = zip.getInputStream(entry)
            if (entry.isDirectory()) {      //处理压缩文件包含文件夹的情况
                val result = File(outDir, zipEntryName)
                result.mkdir()
                continue
            }
            val file = File(outDir, zipEntryName)
            file.createNewFile()
            val out: OutputStream = FileOutputStream(file)
            val buff = ByteArray(1024)
            var len: Int
            while (`in`.read(buff).also { len = it } > 0) {
                out.write(buff, 0, len)
            }
            `in`.close()
            out.close()
        }
        return outFileDir
    }

    /**
     * bytes保存文件
     */

    fun save(dest: String, bytes: ByteArray?): Boolean {
        if (bytes == null || bytes.size == 0) {
            return false
        }
        var bis: ByteArrayInputStream? = null
        var fos: FileOutputStream? = null
        val destFile = File(dest)
        try {
            if (!destFile.exists() && !destFile.createNewFile()) {
                return false
            }
            val buffer = ByteArray(1024)
            bis = ByteArrayInputStream(bytes)
            fos = FileOutputStream(destFile)
            var len: Int
            while (bis.read(buffer, 0, buffer.size).also { len = it } != -1) {
                fos.write(buffer, 0, len)
            }
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return destFile.exists() && dest.length > 0
    }

    fun getImagePathFormUri(context: Context, uri: Uri?): String? {
        if (uri != null) {
            val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
            val c = context.contentResolver.query(uri, filePathColumns, null, null, null)
            var path: String? = null
            if (c != null && c.moveToFirst()) {
                val columnIndex = c.getColumnIndex(filePathColumns[0])
                path = c.getString(columnIndex)
                c.close()
            }
            return path
        }
        return null
    }

    /**
     * 分块文件
     */
    fun getBlock(offset: Long, file: File, blockSize: Int): ByteArray? {
        val result = ByteArray(blockSize)
        var accessFile: RandomAccessFile? = null
        try {
            accessFile = RandomAccessFile(file, "r")
            accessFile.seek(offset)
            val readSize = accessFile.read(result)
            if (readSize == -1) {
                return null
            } else if (readSize == blockSize) {
                return result
            } else {
                val tmpByte = ByteArray(readSize)
                System.arraycopy(result, 0, tmpByte, 0, readSize)
                return tmpByte
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close()
                } catch (e1: IOException) {
                }

            }
        }
        return null
    }

    fun getSuffix(filePath: String): String {
        val lastIndex = filePath.lastIndexOf(".")
        return if (lastIndex < 0 || lastIndex > filePath.length) {
            ""
        } else filePath.substring(lastIndex + 1).toLowerCase(Locale.getDefault())
    }

    /**
     * 文件转MD5字符串
     *
     * @param file
     * @return
     */
    fun getFileMD5(file: File): String {
        if (!file.isFile) {
            return ""
        }
        var digest: MessageDigest? = null
        var `in`: FileInputStream? = null
        val buffer = ByteArray(1024)
        var len: Int
        try {
            digest = MessageDigest.getInstance("MD5")
            `in` = FileInputStream(file)
            while (`in`.read(buffer, 0, 1024).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            `in`.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return ""
        }
        val bigInt = BigInteger(1, digest.digest())
        return bigInt.toString(16)
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild
     * ;true递归子目录中的文件
     * @return
     */
    fun getDirMD5(file: File, listChild: Boolean): Map<String, String>? {
        if (!file.isDirectory) {
            return null
        }
        val map = HashMap<String, String>()
        var md5: String?
        val files = file.listFiles()
        for (i in files.indices) {
            val f = files[i]
            if (f.isDirectory && listChild) {
                getDirMD5(f, listChild)?.let {
                    map.putAll(it)
                }
            } else {
                md5 = getFileMD5(f)
                    map.put(f.path, md5)
            }
        }
        return map
    }

    fun assetsToLocal(context: Context,assetsName:String,file: File){
        var inputStream: InputStream?=null
        var fos: FileOutputStream?=null
        try {
            if (file.exists()){
                file.delete()
            }
            file.createNewFile()
            inputStream =context.assets.open(assetsName)
            fos = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var byteCount: Int
            while (inputStream.read(buffer).also { byteCount = it } != -1) {
                fos.write(buffer, 0, byteCount)
            }
            fos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            fos?.close()
        }
    }

    @Throws(java.lang.Exception::class)
    fun getContent(filePath: String?): ByteArray? {
        val file = File(filePath)
        val fileSize = file.length()
        if (fileSize > Int.MAX_VALUE) {
            println("file too big...")
            return null
        }
        val fi = FileInputStream(file)
        val buffer = ByteArray(fileSize.toInt())
        var offset = 0
        var numRead = 0
        while (offset < buffer.size
            && fi.read(buffer, offset, buffer.size - offset).also { numRead = it } >= 0
        ) {
            offset += numRead
        }
        // 确保所有数据均被读取
        if (offset != buffer.size) {
            throw IOException(
                "Could not completely read file "
                        + file.name
            )
        }
        fi.close()
        return buffer
    }


    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    fun fileToBase64(file: File): String {
        var base64: String = ""
        var imputStream: InputStream? = null
        try {
            imputStream = FileInputStream(file)
            val bytes = ByteArray(imputStream.available())
            val length = imputStream.read(bytes)
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT)
        }catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                imputStream?.reset()
                imputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return base64
    }

    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    fun maxFileToBase64(file: File): String {
        var base64 = ""
        val os1 = ByteArrayOutputStream(file.length().toInt())
        val file1: InputStream = FileInputStream(file)
        try {
            val byteBuf = ByteArray(3 * 1024 * 1024)
            var base64ByteBuf: ByteArray
            var count1: Int //每次从文件中读取到的有效字节数
            while (file1.read(byteBuf).also { count1 = it } != -1) {
                if (count1 != byteBuf.size) { //如果有效字节数不为3*1000，则说明文件已经读到尾了，不够填充满byteBuf了
                    val copy = Arrays.copyOf(byteBuf, count1) //从byteBuf中截取包含有效字节数的字节段
                    base64ByteBuf = Base64.encode(copy,Base64.DEFAULT) //对有效字节段进行编码
                } else {
                    base64ByteBuf = Base64.encode(byteBuf,Base64.DEFAULT)

                }
                os1.write(base64ByteBuf, 0, base64ByteBuf.size)
                os1.flush()
            }
            base64 = os1.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os1.reset()
                os1.close()
                file1.reset()
                file1.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return base64
    }

    fun uriToFile(context: Context, uri: Uri): File? = when(uri.scheme){
        ContentResolver.SCHEME_FILE -> uri.toFile()
        ContentResolver.SCHEME_CONTENT ->{
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                if(it.moveToFirst()){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        //保存到本地
                        val ois = context.contentResolver.openInputStream(uri)
                        val displayName =
                            it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        ois?.let {
                            val file = File(
                                context.externalCacheDir!!.absolutePath,
                                "${Random(9999)}$displayName"
                            )
                            val fos = FileOutputStream(file)
                            val buffer = ByteArray(1024)
                            var len: Int = ois.read(buffer)
                            var downloaded: Long = 0
                            while (len != -1) {
                                fos.write(buffer, 0, len)
                                len = ois.read(buffer)
                            }
                            fos.close()
                            it.close()
                            file
                        }
                    }else
                    //直接转换
                        File(it.getString(it.getColumnIndex(MediaStore.Images.Media.DATA)))
                }else {
                    it.close()
                    null
                }
            }
        }
        else -> null
    }
}