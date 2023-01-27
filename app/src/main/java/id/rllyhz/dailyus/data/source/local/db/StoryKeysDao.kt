package id.rllyhz.dailyus.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rllyhz.dailyus.data.source.local.model.StoryKeys

@Dao
interface StoryKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(storyKeys: List<StoryKeys>)

    @Query("SELECT * FROM story_keys WHERE id = :storyKeysId")
    suspend fun getStoryKeysOf(storyKeysId: String): StoryKeys?

    @Query("DELETE FROM story_keys")
    suspend fun deleteAllKeys()
}