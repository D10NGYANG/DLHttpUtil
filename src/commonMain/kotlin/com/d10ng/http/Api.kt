package com.d10ng.http

import com.d10ng.http.response.ErrorResponse
import com.d10ng.http.response.SerializerFailedErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString

object Api {

    suspend fun handlerResponse(block: suspend (HttpClient) -> HttpResponse): HttpResponse? {
        return try {
            block.invoke(Http.client!!)
        } catch (e: ResponseException) {
            handleResponseException(e)
            e.response
        } catch (e: Exception) {
            handleAnyException(e)
            null
        }
    }

    suspend fun <T> handler(block: suspend (HttpClient) -> T): T? {
        return try {
            block.invoke(Http.client!!)
        } catch (e: ResponseException) {
            handleResponseException(e)
            null
        } catch (e: NoTransformationFoundException) {
            handleNoTransformationFoundException(e)
            null
        } catch (e: Exception) {
            handleAnyException(e)
            null
        }
    }

    /**
     * 处理接口返回错误
     * @param e ResponseException
     */
    private suspend fun handleResponseException(e: ResponseException) {
        val isBodyJson = e.response.contentType()?.match(ContentType.Application.Json) == true
        val body = e.response.bodyAsText()
        val error: ErrorResponse = if (isBodyJson && body.contains("\"message\"")) {
            Http.json.decodeFromString(body)
        } else {
            ErrorResponse(e.response.status.value, "", e.message?: e.response.status.description)
        }
        error.url = e.response.call.request.url.toString()
        Http.postErrorResponse(error)
    }

    /**
     * 处理JSON解析失败错误
     * @param e NoTransformationFoundException
     */
    private fun handleNoTransformationFoundException(e: NoTransformationFoundException) {
        Http.postErrorResponse(SerializerFailedErrorResponse(e).instant)
    }

    /**
     * 处理网络请求其余错误
     * @param e Exception
     */
    private fun handleAnyException(e: Exception) {
        Http.postErrorResponse(ErrorResponse(0, "", e.message?: "网络请求失败"))
    }
}