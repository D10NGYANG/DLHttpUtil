package com.d10ng.http

import com.d10ng.http.exception.InterdictionException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*

object Api {

    var client: HttpClient? = null

    suspend fun handleResponse(
        block: suspend (HttpClient) -> HttpResponse,
    ): HttpResponse? {
        return try {
            block(client!!)
        } catch (e: InterdictionException) {
            Http.postErrorResponse(e.message ?: "")
            null
        } catch (e: Exception) {
            e.printStackTrace()
            Http.postErrorResponse("网络请求失败")
            null
        }
    }

    suspend fun <T> handle(
        block: suspend (HttpClient) -> T,
    ): T? {
        return try {
            block.invoke(client!!)
        } catch (e: InterdictionException) {
            Http.postErrorResponse(e.message ?: "")
            null
        } catch (e: NoTransformationFoundException) {
            e.printStackTrace()
            Http.postErrorResponse("数据解析失败")
            null
        } catch (e: Exception) {
            e.printStackTrace()
            Http.postErrorResponse("网络请求失败")
            null
        }
    }
}