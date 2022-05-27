package com.d10ng.http.response

import io.ktor.client.call.*

class SerializerFailedErrorResponse(e: NoTransformationFoundException) {

    var instant: ErrorResponse

    init {
        val msg = e.message
        instant = if (msg == null) {
            ErrorResponse(0, "","数据解析失败")
        } else {
            var url = ""
            var code = 0
            val urlIndex = msg.indexOf("|with response from ") + 20
            val codeIndex = msg.indexOf("|status: ") + 9
            val endIndex = msg.indexOf("|response headers: ")
            if (urlIndex > 20 && codeIndex > 9 && codeIndex > urlIndex && endIndex > codeIndex) {
                url = msg.substring(urlIndex, codeIndex)
                code = msg.substring(codeIndex, endIndex).toIntOrNull()?: 0
            }
            ErrorResponse(code, url,"数据解析失败")
        }
    }
}