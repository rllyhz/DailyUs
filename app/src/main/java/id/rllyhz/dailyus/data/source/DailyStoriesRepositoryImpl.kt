package id.rllyhz.dailyus.data.source

import id.rllyhz.dailyus.data.source.remote.model.DailyStoryResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class DailyStoriesRepositoryImpl @Inject constructor(
    private val storiesApi: DailyUsStoriesApiService
) : DailyStoriesRepository {

    override fun fetchStories(token: String): Flow<Resource<DailyStoryResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val responseData = storiesApi.getStories(
                    token = "Bearer $token",
                    size = 30,
                    location = 1
                )

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