package com.guoliang.frame.network

import java.lang.Exception

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/7/24 10:04
 */
open class BaseRemoteRepository {

    suspend fun <T :Any> safeApiCall(call: suspend () -> T):ResultKT<T> = try {
        ResultKT.Success(call.invoke())
    }catch (e :Exception){
        e.printStackTrace()
        ResultKT.Error(ExceptionHandle.handleException(e),e.message.toString())
    }



}