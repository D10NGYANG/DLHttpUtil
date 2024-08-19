import com.d10ng.common.transform.json
import com.d10ng.http.exception.InterdictionException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.Test
import java.net.UnknownHostException

@Serializable
data class BaseResult<T>(
    @SerialName("msg")
    var msg: String = "",
    @SerialName("code")
    var code: Int = 0,
    @SerialName("data")
    var data: T? = null
)

@Serializable
data class Error(
    @SerialName("msg")
    var msg: String = "",
    @SerialName("code")
    var code: Int = 0
)

@Serializable
data class ActiveInfo(
    @SerialName("cardNumber")
    var cardNumber: String = "",
    @SerialName("id")
    var id: String = "",
    @SerialName("isActivated")
    var isActivated: Boolean = false,
    @SerialName("lastDataTime")
    var lastDataTime: String = "",
    @SerialName("username")
    var username: String = ""
)

class Test2 {

    // 网络请求客户端
    internal val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                        //Log.i(message)
                    }
                }
                level = LogLevel.ALL
            }
            // 拦截异常
            expectSuccess = true
            HttpResponseValidator {
                validateResponse { response ->
                    val error: Error = response.body()
                    println("error: $error")
                    if (error.code != 200) {
                        println("错误")
                        throw InterdictionException(error.msg)
                    }
                    println("正确")
                }
                handleResponseExceptionWithRequest { cause, _ ->
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
                                    val respText = cause.response.bodyAsText()
                                    val error = try {
                                        json.decodeFromString<BaseResult<Boolean>>(respText).msg
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        null
                                    }
                                    throw InterdictionException(error ?: "网络请求失败")
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
    }

    @Test
    fun test() = runBlocking {
        val res: BaseResult<ActiveInfo>? = client.get("https://cim-pro-api.bds100.com/api/v1/user/activation/info") {
            parameter("cardNumber", "12341234")
        }.body()
        println(res)
    }
}