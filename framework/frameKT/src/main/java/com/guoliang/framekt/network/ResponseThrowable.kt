package com.guoliang.framekt.network

import com.guoliang.framekt.context

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/7/24 9:54
 */
class ResponseThrowable : Exception {
    var code: Int
    var errMsg: String

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        code = error.getKey()
        errMsg = context().getString(error.getValue())
    }

    constructor(code: Int, msgID: Int, e: Throwable? = null) : super(e) {
        this.code = code
        this.errMsg = context().getString(msgID)
    }
}

