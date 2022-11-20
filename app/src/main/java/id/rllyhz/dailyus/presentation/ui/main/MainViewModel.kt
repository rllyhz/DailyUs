package id.rllyhz.dailyus.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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

    fun uploadStory(
        imageFile: File,
        filename: String,
        description: String
    ): LiveData<Resource<UploadStoryResponse>> {
        val token = authPreferences.userToken.value as String

        val reqBodyImage = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val reqBodyDesc = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val reqBodyLat = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        val reqBodyLon = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartImage = MultipartBody.Part.createFormData("photo", filename, reqBodyImage)

        return dailyStoriesRepository.uploadNewStory(
            token,
            multipartImage,
            reqBodyDesc,
            reqBodyLat,
            reqBodyLon
        )
            .asLiveData()
    }

    fun logout() = viewModelScope.launch {
        authPreferences.clear()
    }
}