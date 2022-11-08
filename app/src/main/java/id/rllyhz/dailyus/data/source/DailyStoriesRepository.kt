package id.rllyhz.dailyus.data.source

import androidx.paging.PagingData
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.remote.model.DailyStoryResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface DailyStoriesRepository {

    fun getStories(
        token: String
    ): Flow<PagingData<StoryEntity>>

    fun fetchStories(token: String): Flow<Resource<DailyStoryResponse>>

    fun uploadNewStory(
        token: String,
        photoUrl: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<UploadStoryResponse>>
}