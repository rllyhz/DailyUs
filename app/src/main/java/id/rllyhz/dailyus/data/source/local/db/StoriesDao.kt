package id.rllyhz.dailyus.data.source.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rllyhz.dailyus.data.source.local.model.StoryEntity

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stories: List<StoryEntity>)

    @Query("SELECT * FROM story")
    suspend fun getStories(): List<StoryEntity>

    @Query("SELECT * FROM story")
    suspend fun getPagingStories(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}