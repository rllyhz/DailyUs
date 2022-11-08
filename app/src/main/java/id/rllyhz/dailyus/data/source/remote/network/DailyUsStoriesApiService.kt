package id.rllyhz.dailyus.data.source.remote.network

import id.rllyhz.dailyus.data.source.remote.model.DailyStoryResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DailyUsStoriesApiService {
    /*
     * Retrieve all stories
     *
     * @param token
     * @param page
     * @param size
     * @param location
     *
     * @return DailyStoryResponse
     */
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null,
    ): DailyStoryResponse

    /*
     * Post new story
     *
     * @param token
     * @param photo
     * @param description
     * @param latitude
     * @param longitude
     *
     * @return UploadStoryResponse
     */
    @Multipart
    @POST("stories")
    suspend fun uploadNewStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody?,
        @Part("lon") longitude: RequestBody?,
    ): UploadStoryResponse
}