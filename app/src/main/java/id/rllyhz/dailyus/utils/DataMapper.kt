package id.rllyhz.dailyus.utils

import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.remote.model.StoryListResponse
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun StoryListResponse.toEntity(): StoryEntity =
    StoryEntity(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        latitude = lat,
        longitude = lon
    )

fun List<StoryListResponse>.toEntities(): List<StoryEntity> =
    map { it.toEntity() }

fun formatDate(date: String?): String? {
    if (date == null) return null

    val currentFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val targetFormat = "dd MMM yyyy, HH:mm"
    val timeZone = "GMT"
    val locale = Locale.getDefault()
    val currentDateFormat: DateFormat = SimpleDateFormat(currentFormat, locale)
    currentDateFormat.timeZone = TimeZone.getTimeZone(timeZone)

    val targetDateFormat: DateFormat = SimpleDateFormat(targetFormat, locale)
    var targetDate: String? = null

    try {
        val currentDate = currentDateFormat.parse(date)
        if (currentDate != null) {
            targetDate = targetDateFormat.format(currentDate)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return targetDate.toString()
}