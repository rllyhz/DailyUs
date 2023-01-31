package id.rllyhz.dailyus.data.source.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.utils.DataForTesting
import id.rllyhz.dailyus.utils.PagedTestDataSource

@ExperimentalPagingApi
class FakeStoriesDao : StoriesDao {

    private val _stories: ArrayList<StoryEntity> = ArrayList(DataForTesting.dummyStories())

    override suspend fun insertAll(stories: List<StoryEntity>) {
        _stories.addAll(stories)
    }

    override suspend fun getStories(): List<StoryEntity> = _stories

    override fun getPagingStories(): PagingSource<Int, StoryEntity> =
        PagedTestDataSource(_stories)

    override suspend fun deleteAll() {
        _stories.clear()
    }
}