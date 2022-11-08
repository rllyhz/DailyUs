package id.rllyhz.dailyus.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.rllyhz.dailyus.data.mediator.DailyStoriesMediator
import id.rllyhz.dailyus.data.source.local.db.DailyUsDatabase
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
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
    private val storiesApi: DailyUsStoriesApiService,
    private val db: DailyUsDatabase
) : DailyStoriesRepository {

    @ExperimentalPagingApi
    override fun getStories(token: String): Flow<PagingData<StoryEntity>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = DailyStoriesMediator(
                storiesApi,
                db,
                "Bearer $token"
            ),
            pagingSourceFactory = { db.storiesDao().getStories() }
        ).flow

    override fun fetchStories(token: String): Flow<Resource<DailyStoryResponse>> =
        channelFlow {
            send(Resource.Loading())

            try {
                val responseData = storiesApi.getStories(
                    token = "Bearer $token",
                    size = 30,
                    location = 1
                )
                send(Resource.Success(responseData))

            } catch (e: Exception) {
                send(Resource.Error(e.message.toString()))
            }
        }

    override fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<UploadStoryResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val responseData = storiesApi.uploadNewStory(
                    "Bearer $token",
                    photo,
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