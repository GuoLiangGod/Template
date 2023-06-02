package com.guoliang.framekt

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/12/21 11:10
 */
internal class ContextProvider : ContentProvider(){

    override fun onCreate(): Boolean {
        init(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

}
// Context.kt
private lateinit var application : Context

fun init(context : Context){
    application= context
}

fun context() : Context{
    return application
}