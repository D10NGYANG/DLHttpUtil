package com.d10ng.http

import com.d10ng.http.exception.UnauthorizedException
import com.d10ng.http.response.ErrorResponse
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object Http {

    /** 自定义规则的JSON工具 */
    val json by lazy {
        Json {
            // 忽略JSON字符串里有但data class中没有的key
            ignoreUnknownKeys = true
            // 如果接收到的JSON字符串的value为null，但是data class中的对应属性不能为null，那就使用属性的默认值
            coerceInputValues = true
            // 如果创建data class实例时有些属性没有赋值，那就使用默认值进行转换成JSON字符串
            encodeDefaults = true
            // 属性放宽
            isLenient = true
        }
    }

    /** 网络请求客户端 */
    var client: HttpClient? = null

    /**
     * 初始化
     * @param isLogEnable Boolean 是否开启打印
     * @param handleResponseException SuspendFunction2<[@kotlin.ParameterName] Throwable, [@kotlin.ParameterName] HttpRequest, Unit> 错误处理
     */
    fun init(
        isLogEnable: Boolean,
        handleResponseException: suspend (cause: Throwable, request: HttpRequest) -> Unit = { _, _ -> }
    ) {
        if (client != null) {
            client?.close()
        }
        client = HttpClient() {
            install(ContentNegotiation) {
                json(json = this@Http.json)
            }
            install(Logging) {
                logger = object: Logger {
                    override fun log(message: String) {
                        Napier.i(message,null, "HTTP Client")
                    }
                }
                level = if (isLogEnable) LogLevel.ALL else LogLevel.NONE
            }
            expectSuccess = true
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    when(cause) {
                        is ClientRequestException -> {
                            val exceptionResponse = cause.response
                            when(exceptionResponse.status) {
                                HttpStatusCode.Unauthorized -> {
                                    val exceptionResponseText = exceptionResponse.bodyAsText()
                                    throw UnauthorizedException(exceptionResponse, exceptionResponseText)
                                }
                                HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
                                    throw Exception("服务器资源不存在或已禁止访问")
                                }
                                else -> {
                                    if (exceptionResponse.status.value >= HttpStatusCode.InternalServerError.value) {
                                        throw Exception("服务器无响应，请检查网络后再试")
                                    } else {
                                        handleResponseException(cause, request)
                                    }
                                }
                            }
                        }
                        is ServerResponseException -> {
                            throw Exception("服务器无响应，请检查网络后再试")
                        }
                        is CancellationException -> {
                            // 协程被取消的错误，不用管
                            throw Exception("请求任务已取消")
                        }
                        is ConnectTimeoutException -> {
                            throw Exception("访问服务器超时，请检查网络后再试")
                        }
                        else -> {
                            println("网络请求错误, $cause")
                            handleResponseException(cause, request)
                        }
                    }
                }
            }
        }.also { Napier.base(DebugAntilog()) }
    }

    /** 释放 */
    fun release() {
        client?.close()
    }

    /** 网络请求错误监听器 */
    val errorResponseFlow = MutableSharedFlow<ErrorResponse>()

    /**
     * 推送错误信息
     * @param e ResponseException
     */
    fun postErrorResponse(e: ErrorResponse) {
        CoroutineScope(Dispatchers.Default).launch {
            errorResponseFlow.emit(e)
        }
    }
}

/**
 * 判断返回值是否是成功
 * @receiver HttpResponse
 * @return Boolean
 */
fun HttpResponse.isSuccess() = status.value in HttpStatusCode.OK.value .. HttpStatusCode.MultiStatus.value