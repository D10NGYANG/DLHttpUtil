import com.d10ng.common.log.LogIt
import com.d10ng.http.Api
import com.d10ng.http.Http
import com.d10ng.http.setDefaultHttpResponseValidator
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.Test

object LogTest : LogIt("test")

class TestCommon {

    @Test
    fun test() {
        runBlocking {
            launch {
                LogTest.i("result ${Http.errorResponseMessageFlow.first()}")
            }
            LogTest.debug = true
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(json = com.d10ng.common.transform.json)
                }
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            LogTest.i(message)
                        }
                    }
                    level = LogLevel.ALL
                }
                setDefaultHttpResponseValidator()
            }
            Api.client = client
            Api.handle<AppLastVersion> {
                it.get("https://eim-prod-api.bds100.com/api/app/version/EIMS").body()
            }?.apply {
                println(this)
            }
            client.close()
        }
    }
}

@Serializable
data class AppLastVersion(
    @SerialName("code")
    var code: Int = 0,
    @SerialName("createBy")
    var createBy: String = "",
    @SerialName("createTime")
    var createTime: String = "",
    @SerialName("description")
    var description: String = "",
    @SerialName("id")
    var id: Long = 0,
    @SerialName("name")
    var name: String = "",
    @SerialName("size")
    var size: String = "",
    @SerialName("type")
    var type: String = "",
    @SerialName("updateBy")
    var updateBy: String = "",
    @SerialName("updateTime")
    var updateTime: String = "",
    @SerialName("url")
    var url: String = ""
)