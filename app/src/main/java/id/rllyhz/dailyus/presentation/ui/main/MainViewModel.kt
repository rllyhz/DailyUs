package id.rllyhz.dailyus.presentation.ui.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import id.rllyhz.dailyus.data.source.local.db.DailyStoriesDatabase
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
    private val dailyStoriesRepository: DailyStoriesRepository,
    private val dailyStoriesDatabase: DailyStoriesDatabase
) : ViewModel() {

    val storiesWithLocation = MutableLiveData<Resource<List<StoryEntity>>>()
    val uploadStoryResponse = MutableLiveData<Resource<UploadStoryResponse>>()

    private var _shouldHandleUploadStoryEvent = true
    var scrollToTopEventCallback: (() -> Unit)? = null

    var lastKnownLocation: Location? = null

    fun shouldHandleUploadStoryEvent(): Boolean = if (_shouldHandleUploadStoryEvent) {
        _shouldHandleUploadStoryEvent = false
        true
    } else false

    fun addScrollToTopEvent() {
        scrollToTopEventCallback?.invoke()
    }

    fun getToken(): LiveData<String> = authPreferences.userToken

    fun getFullName(): LiveData<String> = authPreferences.userFullName

    fun getEmail(): LiveData<String> = authPreferences.userEmail

    fun loadPagingStories(token: String) =
        dailyStoriesRepository.getPagingStories(token).cachedIn(viewModelScope)

    fun loadStoriesWithLocation(token: String) = viewModelScope.launch(Dispatchers.IO) {
        storiesWithLocation.postValue(Resource.Loading())

        dailyStoriesRepository.fetchStoriesWithLocation(token).cancellable()
            .collectLatest { result ->
                when (result) {
                    is Resource.Error -> storiesWithLocation.postValue(
                        Resource.Error(
                            result.message ?: "Error"
                        )
                    )
                    is Resource.Loading -> storiesWithLocation.postValue(Resource.Loading())
                    is Resource.Success -> {
                        val data = result.data

                        if (data.isNullOrEmpty()) {
                            storiesWithLocation.postValue(Resource.Success(emptyList()))
                        } else {
                            storiesWithLocation.postValue(Resource.Success(data))
                        }
                    }
                    else -> Unit
                }
            }
    }

    fun uploadStory(
        token: String,
        imageFile: File, filename: String, description: String,
        imageTooLargeCallback: (size: Double) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        uploadStoryResponse.postValue(Resource.Loading())

        _shouldHandleUploadStoryEvent = true

        val compressedImageFile = imageFile.getCompressedImageFile()
        val size = compressedImageFile.sizeInMb

        if (size > 1) {
            uploadStoryResponse.postValue(Resource.Initial())
            imageTooLargeCallback.invoke(size.formattedSize)
            cancel()
            return@launch
        }

        val reqBodyImage = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val reqBodyDesc = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartImage = MultipartBody.Part.createFormData("photo", filename, reqBodyImage)

        var latitude = 0.0
        var longitude = 0.0

        lastKnownLocation?.let {
            latitude = it.latitude
            longitude = it.longitude
        }

        val reqBodyLon = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val reqBodyLat = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

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
                else -> Unit
            }
        }
    }

    fun logout() = viewModelScope.launch {
        authPreferences.clear()
        dailyStoriesDatabase.getStoriesDao().deleteAll()
    }
}