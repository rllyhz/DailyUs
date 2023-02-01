package id.rllyhz.dailyus.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class DailyStoryResponse(
    @SerializedName("listStory")
    val listStory: List<StoryListResponse>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)

data class StoryListResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("photoUrl")
    val photoUrl: String,
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("lat")
    val lat: Double,
    @field:SerializedName("lon")
    val lon: Double
)