package id.rllyhz.dailyus.data.source.local

import id.rllyhz.dailyus.data.source.local.db.StoryKeysDao
import id.rllyhz.dailyus.data.source.local.model.StoryKeys
import id.rllyhz.dailyus.utils.DataForTesting

class FakeStoryKeysDao : StoryKeysDao {

    private val _storyKeys: ArrayList<StoryKeys> = ArrayList(DataForTesting.dummyStoryKeys())

    override suspend fun insertKeys(storyKeys: List<StoryKeys>) {
        _storyKeys.addAll(storyKeys)
    }

    override suspend fun getStoryKeysOf(storyKeysId: String): StoryKeys? =
        _storyKeys.firstOrNull { it.id == storyKeysId }

    override suspend fun deleteAllKeys() {
        _storyKeys.clear()
    }
}