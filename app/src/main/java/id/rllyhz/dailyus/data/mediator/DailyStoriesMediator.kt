package id.rllyhz.dailyus.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.rllyhz.dailyus.data.source.local.db.DailyUsDatabase
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.data.source.local.model.StoryKeys
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class DailyStoriesMediator(
    private val storiesApi: DailyUsStoriesApiService,
    private val db: DailyUsDatabase,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val pageKey = when (loadType) {
            LoadType.REFRESH -> getRemoteKeyClosestToCurrentPosition(state)?.nextKey?.minus(1) ?: 1

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )

                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if TopTVRemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )

                nextKey
            }
        }

        try {
            val responseData = storiesApi.getStories(
                token = token,
                page = pageKey,
                size = state.config.pageSize
            )

            val stories = responseData.stories
            val endOfPaginationReached = stories.isEmpty()

            db.withTransaction {
                // if on refresh state, clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    db.storyKeysDao().deleteKeys()
                    db.storiesDao().deleteStories()
                }

                val prevKey = if (pageKey == 1) null else pageKey - 1
                val nextKey = if (endOfPaginationReached) null else pageKey + 1
                val keys = stories.map { StoryKeys(it.id, prevKey, nextKey) }

                val storiesForLocal = arrayListOf<StoryEntity>()
                stories.map {
                    StoryEntity(
                        it.id,
                        it.name,
                        it.description,
                        it.photoUrl,
                        it.createdAt,
                        it.latitude,
                        it.longitude,
                    ).let { storyEntities -> storiesForLocal.add(storyEntities) }
                }

                db.storyKeysDao().insertKeys(keys)
                db.storiesDao().insertStories(storiesForLocal)
            }

            return MediatorResult.Success(endOfPaginationReached)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            db.storyKeysDao().getKeysOf(it.id)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            db.storyKeysDao().getKeysOf(it.id)
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): StoryKeys? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.storyKeysDao().getKeysOf(id)
            }
        }
}