package id.rllyhz.dailyus.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.utils.formattedSize
import id.rllyhz.dailyus.utils.getCompressedImageFile
import id.rllyhz.dailyus.utils.sizeInMb
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
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

    val stories = MutableLiveData<Resource<List<StoryEntity>>>()
    val uploadStoryResponse = MutableLiveData<Resource<UploadStoryResponse>>()

    private var _shouldHandleUploadStoryEvent = true

    fun shouldHandleUploadStoryEvent(): Boolean {
        return if (_shouldHandleUploadStoryEvent) {
            _shouldHandleUploadStoryEvent = false
            true
        } else {
            false
        }
    }

    fun getFullName(): LiveData<String> = authPreferences.userFullName

    fun getEmail(): LiveData<String> = authPreferences.userEmail

    fun loadStories() = viewModelScope.launch(Dispatchers.IO) {
        stories.postValue(Resource.Loading())

        val token = authPreferences.getUserToken()

        dailyStoriesRepository.fetchStories(token).cancellable().collectLatest { result ->
            when (result) {
                is Resource.Error -> stories.postValue(
                    Resource.Error(
                        result.message ?: "Error"
                    )
                )
                is Resource.Loading -> stories.postValue(Resource.Loading())
                is Resource.Success -> {
                    val data = result.data

                    if (data.isNullOrEmpty()) {
                        stories.postValue(Resource.Success(emptyList()))
                    } else {
                        stories.postValue(Resource.Success(data))
                    }
                }
            }
        }
    }

    fun uploadStory(
        imageFile: File, filename: String, description: String,
        imageTooLargeCallback: (size: Double) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        _shouldHandleUploadStoryEvent = true

        val compressedImageFile = imageFile.getCompressedImageFile()
        val size = compressedImageFile.sizeInMb

        if (size > 1) {
            imageTooLargeCallback.invoke(size.formattedSize)
            cancel()
            return@launch
        }

        uploadStoryResponse.postValue(Resource.Loading())

        val token = authPreferences.getUserToken()

        val reqBodyImage = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val reqBodyDesc = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val reqBodyLat = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        val reqBodyLon = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartImage = MultipartBody.Part.createFormData("photo", filename, reqBodyImage)

        dailyStoriesRepository.uploadNewStory(
            token, multipartImage, reqBodyDesc, reqBodyLat, reqBodyLon
        ).cancellable().collectLatest { result ->
            when (result) {
                is Resource.Error -> {
                    uploadStoryResponse.postValue(
                        Resource.Error(
                            result.message ?: "Error"
                        )
                    )
                }
                is Resource.Loading -> uploadStoryResponse.postValue(Resource.Loading())
                is Resource.Success -> {
                    val data = result.data ?: UploadStoryResponse(false, "")
                    uploadStoryResponse.postValue(Resource.Success(data))
                }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        authPreferences.clear()
    }
}