package com.d10ng.http.response

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("status")
    var statusCode: Int = HttpStatusCode.BadGateway.value,

    var url: String = "",

    @SerialName("message")
    var message: String = ""
)