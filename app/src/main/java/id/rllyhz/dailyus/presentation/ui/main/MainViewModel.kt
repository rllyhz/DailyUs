package id.rllyhz.dailyus.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val dailyStoriesRepository: DailyStoriesRepository
) : ViewModel() {

    fun getToken(): LiveData<String> = authPreferences.userToken

    fun getFullName(): LiveData<String> = authPreferences.userFullName

    fun getEmail(): LiveData<String> = authPreferences.userEmail

    fun fetchStories(token: String) = dailyStoriesRepository.fetchStories(token)

    fun logout() = viewModelScope.launch {
        authPreferences.clear()
    }
}