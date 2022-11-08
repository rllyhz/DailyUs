package id.rllyhz.dailyus.data.source.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String,

    val photoUrl: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "lat")
    val latitude: Double?,

    @ColumnInfo(name = "lon")
    val longitude: Double?

) : Parcelable
