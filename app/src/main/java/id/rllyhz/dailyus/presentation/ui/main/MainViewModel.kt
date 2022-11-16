package id.rllyhz.dailyus.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authPreferences: AuthPreferences
) : ViewModel() {

    fun getFullName(): LiveData<String> = authPreferences.userFullName

    fun getEmail(): LiveData<String> = authPreferences.userEmail

    fun logout() = viewModelScope.launch {
        authPreferences.clear()
    }
}