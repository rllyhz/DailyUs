package id.rllyhz.dailyus.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isLoggedIn(): LiveData<Boolean> = authPreferences.isLoggedIn

    fun saveLoggedInUserData(
        token: String, name: String, email: String
    ) = viewModelScope.launch {
        authPreferences.apply {
            saveUserFullName(name)
            saveUserEmail(email)
            saveUserToken(token)
            isLoggedIn(true)
        }
    }

    fun login(
        email: String, password: String
    ) = authRepository.loginUser(email, password).asLiveData()

    fun register(
        fullName: String, email: String, password: String
    ) = authRepository.registerNewUser(
        name = fullName,
        email = email,
        password = password
    ).asLiveData()
}