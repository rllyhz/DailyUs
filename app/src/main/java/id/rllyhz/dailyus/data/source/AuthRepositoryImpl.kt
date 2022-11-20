package id.rllyhz.dailyus.data.source

import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResponse
import id.rllyhz.dailyus.data.source.remote.model.AuthRegisterResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsAuthApiService
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: DailyUsAuthApiService
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthLoginResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val userMap = mapOf(
                    "email" to email,
                    "password" to password,
                )

                val responseData = authApi.loginUser(userMap)

                if (responseData.isError) {
                    emit(Resource.Error(responseData.message))
                } else {
                    emit(Resource.Success(responseData))
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                if (errorBody != null) {
                    val responseJson = JSONObject(errorBody)
                    val message = responseJson.getString("message")
                    // val isError = responseJson.getBoolean("error")
                    emit(Resource.Error(message))
                } else {
                    emit(Resource.Error(e.message.toString()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }

    override fun registerNewUser(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<AuthRegisterResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val newUserMap = mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )

                val responseData = authApi.registerNewUser(newUserMap)

                if (responseData.isError) {
                    emit(Resource.Error(responseData.message))
                } else {
                    emit(Resource.Success(responseData))
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                if (errorBody != null) {
                    val responseJson = JSONObject(errorBody)
                    val message = responseJson.getString("message")
                    // val isError = responseJson.getBoolean("error")
                    emit(Resource.Error(message))
                } else {
                    emit(Resource.Error(e.message.toString()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
}