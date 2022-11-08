package id.rllyhz.dailyus.data.source.remote.network

import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResponse
import id.rllyhz.dailyus.data.source.remote.model.AuthRegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DailyUsApiService {
    /*
     * Register new user
     *
     * @param newUser Map<String, String>
     * @return AuthRegisterResponse
     */
    @POST("register")
    suspend fun registerNewUser(
        @Body newUser: Map<String, String>
    ): AuthRegisterResponse

    /*
     * Login user
     *
     * @param user Map<String, String>
     * @return AuthLoginResponse
     */
    @POST("login")
    suspend fun loginUser(
        @Body user: Map<String, String>
    ): AuthLoginResponse
}