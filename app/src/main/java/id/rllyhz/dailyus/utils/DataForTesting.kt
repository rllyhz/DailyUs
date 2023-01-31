package id.rllyhz.dailyus.utils

import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResponse
import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResult
import id.rllyhz.dailyus.data.source.remote.model.AuthRegisterResponse
import id.rllyhz.dailyus.data.source.remote.model.UploadStoryResponse
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
}