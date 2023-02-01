package id.rllyhz.dailyus.data.source.remote.model

data class AuthLoginResponse(
    val loginResult: AuthLoginResult,
    val error: Boolean,
    val message: String
)

data class AuthLoginResult(
    val name: String,
    val userId: String,
    val token: String,
)