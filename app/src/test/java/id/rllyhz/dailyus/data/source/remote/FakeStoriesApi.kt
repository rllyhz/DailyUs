package id.rllyhz.dailyus.data.source.remote

import id.rllyhz.dailyus.data.source.remote.model.DailyStoryResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.utils.DataForTesting
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeStoriesApi : DailyUsStoriesApiService {

    override suspend fun fetchStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): DailyStoryResponse = DataForTesting.dummyFetchStories()

    override suspend fun uploadNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): UploadStoryResponse = DataForTesting.dummyUploadStory()
}