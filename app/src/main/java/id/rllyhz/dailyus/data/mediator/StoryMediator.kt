package id.rllyhz.dailyus.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.rllyhz.dailyus.data.source.local.db.DailyStoriesDatabase
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.local.model.StoryKeys
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val storiesApi: DailyUsStoriesApiService,
    private val storiesDB: DailyStoriesDatabase,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if TopTVRemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = storiesApi.fetchStories(
                token = token,
                page = page,
                size = state.config.pageSize
            )
            val stories = response.listStory
            val endOfPaginationReached = stories.isEmpty()

            storiesDB.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    storiesDB.getStoryKeysDao().deleteAllKeys()
                    storiesDB.getStoriesDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = stories.map {
                    StoryKeys(it.id, prevKey, nextKey)
                }

                val local = ArrayList<StoryEntity>()

                stories.map { _stories ->
                    StoryEntity(
                        _stories.id,
                        _stories.name,
                        _stories.description,
                        _stories.photoUrl,
                        _stories.createdAt,
                        _stories.lat,
                        _stories.lon,
                    ).let { local.add(it) }
                }
                storiesDB.getStoryKeysDao().insertKeys(keys)
                storiesDB.getStoriesDao().insertAll(local)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    // Get the last page that was retrieved, that contained items.
    // From that last page, get the last item
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storiesDB.getStoryKeysDao().getStoryKeysOf(data.id)
        }

    // Get the first page that was retrieved, that contained items.
    // From that first page, get the first item
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storiesDB.getStoryKeysDao().getStoryKeysOf(data.id)
        }

    // The paging library is trying to load data after the anchor position
    // Get the item closest to the anchor position
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storiesDB.getStoryKeysDao().getStoryKeysOf(id)
            }
        }
}