package id.rllyhz.dailyus.data.source.remote.model

import com.squareup.moshi.Json

data class UploadStoryResponse(
    @Json(name = "error")
    val isError: Boolean,

    @Json(name = "message")
    val message: String
)
