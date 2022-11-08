package id.rllyhz.dailyus.data.source.remote.model

import com.squareup.moshi.Json

data class AuthLoginResponse(
    @Json(name = "loginResult")
    val authLoginResult: AuthLoginResult,

    @Json(name = "error")
    val isError: Boolean,

    @Json(name = "message")
    val message: String
)

data class AuthLoginResult(
    @Json(name = "name")
    val name: String,

    @Json(name = "userId")
    val userId: String,

    @Json(name = "token")
    val token: String,
)