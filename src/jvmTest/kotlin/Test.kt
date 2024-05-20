import com.d10ng.http.Api
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
            val client = HttpClient(CIO) {
                expectSuccess = false
            }
            val response: HttpResponse = client.get("https://eim-prod-api.bds100.com/api/app/versions/EIM")
            println(response.status)
            println(response.bodyAsText())
            client.close()
        }
    }

    @Test
    fun test1() {
        runBlocking {
            val res = Api.handleResponse {
                it.get("https://eim-prod-api.bds100.com/api/app/version/EIM").body()
            }
            println(res)
        }
    }
}