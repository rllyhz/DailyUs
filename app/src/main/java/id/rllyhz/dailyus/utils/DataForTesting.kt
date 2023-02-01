package id.rllyhz.dailyus.utils

import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.local.model.StoryKeys
import id.rllyhz.dailyus.data.source.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataForTesting {
    const val dummyName = "userTest"
    const val dummyEmail = "userTest@mail.com"
    const val dummyPassword = "userTest000"
    const val dummyToken = "randomized-characters-or-numbers-token-here"
    val dummyPhoto = MultipartBody.Part.create("photo-test".toRequestBody())
    val dummyDescription = "description-test".toRequestBody()
    private const val dummyUserId = "id-test-1"
    private const val dummySuccessMessage = "Success"

    fun dummyStories(): List<StoryEntity> {
        val listStories = ArrayList<StoryEntity>()

        for (i in 1..20) {
            val story = StoryEntity(
                id = "id-$i",
                name = "user$i",
                photoUrl = "photoUrl$i",
                createdAt = "createdAt$i",
                description = "description$i",
                latitude = 0.0,
                longitude = 0.0,
            )
            listStories.add(story)
        }

        return listStories
    }

    fun dummyStoryKeys(): List<StoryKeys> {
        val storyKeys = ArrayList<StoryKeys>()

        for (i in 1..10) {
            val storyKey = StoryKeys(
                "id-$i",
                i,
                i + 1
            )
            storyKeys.add(storyKey)
        }

        return storyKeys
    }

    fun dummyRegister(): AuthRegisterResponse =
        AuthRegisterResponse(false, dummySuccessMessage)

    fun dummyLogin(): AuthLoginResponse {
        val loginResult = AuthLoginResult(
            dummyName,
            dummyUserId,
            dummyToken
        )

        return AuthLoginResponse(
            loginResult,
            false,
            dummySuccessMessage
        )
    }

    fun dummyUploadStory(): UploadStoryResponse =
        UploadStoryResponse(false, dummySuccessMessage)

    private fun getStoriesResponseList(): List<StoryListResponse> {
        val data = ArrayList<StoryListResponse>()

        dummyStories().forEach {
            data.add(
                StoryListResponse(
                    it.id,
                    it.name,
                    it.description,
                    it.photoUrl,
                    it.createdAt,
                    it.latitude ?: 0.0,
                    it.longitude ?: 0.0
                )
            )
        }

        return data
    }

    fun dummyFetchStories(): DailyStoryResponse =
        DailyStoryResponse(
            getStoriesResponseList(),
            false,
            dummySuccessMessage
        )
}