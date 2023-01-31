package id.rllyhz.dailyus.data.source.remote

import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResponse
import id.rllyhz.dailyus.data.source.remote.model.AuthRegisterResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsAuthApiService
import id.rllyhz.dailyus.utils.DataForTesting

class FakeAuthApi : DailyUsAuthApiService {
    override suspend fun registerNewUser(newUser: Map<String, String>): AuthRegisterResponse =
        DataForTesting.dummyRegister()

    override suspend fun loginUser(user: Map<String, String>): AuthLoginResponse =
        DataForTesting.dummyLogin()
}