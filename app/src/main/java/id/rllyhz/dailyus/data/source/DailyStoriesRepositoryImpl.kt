package id.rllyhz.dailyus.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.rllyhz.dailyus.data.mediator.StoryMediator
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.data.source.local.db.StoryKeysDao
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.utils.toEntities
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class DailyStoriesRepositoryImpl @Inject constructor(
    private val storiesApi: DailyUsStoriesApiService,
    private val storiesDao: StoriesDao,
    private val storyKeysDao: StoryKeysDao
) : DailyStoriesRepository {

    override fun fetchStoriesWithLocation(token: String): Flow<Resource<List<StoryEntity>>> =
        flow {
            emit(Resource.Loading())

            val storiesInDB = storiesDao.getStories()

            try {
                val responseData = storiesApi.fetchStories(
                    token = "Bearer $token",
                    size = 30,
                    location = 1
                )

                val stories = responseData.listStory.toEntities()

                if (stories.isEmpty() && storiesInDB.isEmpty()) {
                    emit(Resource.Error(responseData.message))
                } else if (stories.isNotEmpty()) {
                    emit(Resource.Success(stories))
                } else {
                    emit(Resource.Success(storiesInDB))
                }

            } catch (e: UnknownHostException) {
                if (storiesInDB.isNotEmpty()) {
                    emit(Resource.Success(storiesInDB))
                } else {
                    emit(Resource.Error(e.message.toString()))
                }
            } catch (e: HttpException) {
                println("HttpException")
                if (storiesInDB.isNotEmpty()) {
                    emit(Resource.Success(storiesInDB))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()

                    if (errorBody != null) {
                        val responseJson = JSONObject(errorBody)
                        val message = responseJson.getString("message")
                        // val isError = responseJson.getBoolean("error")
                        emit(Resource.Error(message))
                    } else {
                        emit(Resource.Error(e.message.toString()))
                    }
                }
            } catch (e: IOException) {
                if (storiesInDB.isNotEmpty()) {
                    emit(Resource.Success(storiesInDB))
                } else {
                    emit(Resource.Error(e.message.toString()))
                }
                println("IOException: " + e.message.toString())
            } catch (e: Exception) {
                if (storiesInDB.isNotEmpty()) {
                    emit(Resource.Success(storiesInDB))
                } else {
                    emit(Resource.Error(e.message.toString()))
                }
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

                if (responseData.error) {
                    emit(Resource.Error(responseData.message))
                } else {
                    emit(Resource.Success(responseData))
                }

            } catch (e: UnknownHostException) {
                emit(Resource.Error("No Internet"))
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

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingStories(token: String): Flow<PagingData<StoryEntity>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryMediator(
                storiesApi,
                storiesDao,
                storyKeysDao,
                "Bearer $token"
            ),
            pagingSourceFactory = { storiesDao.getPagingStories() }
        ).flow
}