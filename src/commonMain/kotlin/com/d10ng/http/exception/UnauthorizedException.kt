package com.d10ng.http.exception

import io.ktor.client.plugins.*
import io.ktor.client.statement.*

class UnauthorizedException(response: HttpResponse, cachedResponseText: String): ResponseException(response, cachedResponseText) {
    override val message: String = "您的登录信息已过期，请重新登录"
}
