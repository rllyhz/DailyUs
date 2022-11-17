package id.rllyhz.dailyus.data.source.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryEntity(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val latitude: Double?,
    val longitude: Double?

) : Parcelable
