package id.rllyhz.dailyus.data.source

import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResponse
import id.rllyhz.dailyus.data.source.remote.model.AuthRegisterResponse
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /*
     * Login
     *
     * @param email String
     * @param password String
     *
     * @return Flow<Resource<AuthLoginResponse>>
     */
    fun loginUser(
        email: String,
        password: String
    ): Flow<Resource<AuthLoginResponse>>

    /*
     * Register new user
     *
     * @param name String
     * @param email String
     * @param password String
     *
     * @return Flow<Resource<AuthRegisterResponse>>
     */
    fun registerNewUser(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<AuthRegisterResponse>>
}