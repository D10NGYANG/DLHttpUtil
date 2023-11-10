import com.d10ng.http.Api
import com.d10ng.http.Http
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class Test {

    @Test
    fun test() {
        runBlocking {
            val client = HttpClient(CIO)
            val response: HttpResponse = client.get("https://ktor.io/")
            println(response.status)
            client.close()
        }
    }

    @Test
    fun test1() {
        runBlocking {
            Http.init(true)
            val res = Api.handlerResponse {
                it.get("https://eim-prod-api.bds100.com/api/app/version/EIM").body()
            }
            println(res)
            Http.release()
        }
    }
}