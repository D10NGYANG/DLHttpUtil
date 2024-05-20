package com.d10ng.http

import com.d10ng.http.exception.InterdictionException
import io.ktor.client.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

object Http {

    private val scope = CoroutineScope(Dispatchers.Default)

    /** 网络请求错误监听器 */
    val errorResponseMessageFlow = MutableSharedFlow<String>()

    /**
     * 推送错误信息
     * @param e String
     */
    fun postErrorResponse(e: String) {
        scope.launch {
            errorResponseMessageFlow.emit(e)
        }
    }
}

/**
 * 判断返回值是否是成功
 * @receiver HttpResponse
 * @return Boolean
 */
fun HttpResponse.isSuccess() = status.value in HttpStatusCode.OK.value .. HttpStatusCode.MultiStatus.value

/**
 * 配置默认的错误处理
 * @receiver HttpClientConfig<*>
 * @param handleException SuspendFunction2<[@kotlin.ParameterName] Throwable, [@kotlin.ParameterName] HttpRequest, Unit>
 */
fun HttpClientConfig<*>.setDefaultHttpResponseValidator(
    handleException: suspend (cause: Throwable, request: HttpRequest) -> Unit = { _, _ -> }
) {
    expectSuccess = true
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, request ->
            when (cause) {
                is RedirectResponseException -> {
                    throw InterdictionException("重定向错误")
                }

                is ClientRequestException -> {
                    val exceptionResponse = cause.response
                    when (exceptionResponse.status) {
                        HttpStatusCode.Unauthorized -> {
                            throw InterdictionException("您的登录信息已过期，请重新登录")
                        }

                        HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
                            throw InterdictionException("服务器资源不存在或已禁止访问")
                        }

                        else -> {
                            handleException(cause, request)
                        }
                    }
                }

                is ServerResponseException -> {
                    throw InterdictionException("服务器无响应，请检查网络后再试")
                }

                is CancellationException -> {
                    // 协程被取消的错误，不用管
                    throw InterdictionException("网络请求已取消")
                }

                is ConnectTimeoutException -> {
                    throw InterdictionException("访问服务器超时，请检查网络后再试")
                }

                is ClosedReceiveChannelException -> {
                    throw InterdictionException("服务器无响应，请检查网络后再试")
                }

                is UnknownHostException -> {
                    throw InterdictionException("无法访问服务器，请检查网络后再试")
                }
            }
        }
    }
}