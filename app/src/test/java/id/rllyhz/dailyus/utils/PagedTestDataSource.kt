package id.rllyhz.dailyus.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.rllyhz.dailyus.data.source.local.model.StoryEntity

class PagedTestDataSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> =
        LoadResult.Page(emptyList(), 0, 1)

    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> =
            PagingData.from(items)
    }
}