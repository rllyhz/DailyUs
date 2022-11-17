package id.rllyhz.dailyus.data.source

import id.rllyhz.dailyus.data.source.remote.model.DailyStoryResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class DailyStoriesRepositoryImpl @Inject constructor(
    private val storiesApi: DailyUsStoriesApiService
) : DailyStoriesRepository {

    override fun fetchStories(token: String): Flow<Resource<DailyStoryResponse>> =
        channelFlow {
            send(Resource.Loading())

            val responseData = storiesApi.getStories(
                token = "Bearer $token",
                size = 30,
                location = 1
            )
            send(Resource.Success(responseData))
        }

    override fun uploadNewStory(
        token: String,
        photoUrl: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<UploadStoryResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val responseData = storiesApi.uploadNewStory(
                    "Bearer $token",
                    photoUrl,
                    description,
                    latitude,
                    longitude
                )
                emit(Resource.Success(responseData))

            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
}