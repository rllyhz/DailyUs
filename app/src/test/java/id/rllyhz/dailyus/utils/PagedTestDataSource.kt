package id.rllyhz.dailyus.utils

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.rllyhz.dailyus.data.source.local.model.StoryEntity

class PagedTestDataSource(private val data: List<StoryEntity>) : PagingSource<Int, StoryEntity>() {
    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> =
        LoadResult.Page(data, 0, 1)

    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> =
            PagingData.from(items)
    }
}