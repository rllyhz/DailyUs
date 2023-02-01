package id.rllyhz.dailyus.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.local.model.StoryKeys

@Database(
    entities = [StoryEntity::class, StoryKeys::class],
    version = 1,
    exportSchema = false
)
abstract class DailyStoriesDatabase : RoomDatabase() {
    abstract fun getStoriesDao(): StoriesDao
    abstract fun getStoryKeysDao(): StoryKeysDao
}