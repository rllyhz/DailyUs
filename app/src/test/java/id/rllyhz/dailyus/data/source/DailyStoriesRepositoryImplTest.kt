package id.rllyhz.dailyus.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import id.rllyhz.dailyus.data.source.remote.FakeStoriesApi
import id.rllyhz.dailyus.data.source.local.FakeStoriesDao
import id.rllyhz.dailyus.data.source.local.FakeStoryKeysDao
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.data.source.local.db.StoryKeysDao
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.utils.CoroutinesTestRule
import id.rllyhz.dailyus.utils.DataForTesting
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner.Silent::class)
class DailyStoriesRepositoryImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var storiesApi: DailyUsStoriesApiService
    private lateinit var storiesDao: StoriesDao
    private lateinit var storyKeysDao: StoryKeysDao
    private lateinit var storiesRepository: DailyStoriesRepositoryImpl

    @Before
    fun setUp() {
        storiesApi = FakeStoriesApi()
        storiesDao = FakeStoriesDao()
        storyKeysDao = FakeStoryKeysDao()
        storiesRepository = DailyStoriesRepositoryImpl(
            storiesApi, storiesDao, storyKeysDao
        )
    }

    @Test
    fun `fetch stories with location`() = runTest {
        val expectedResponse = Resource.Success(DataForTesting.dummyStories())
        val result = storiesRepository.fetchStoriesWithLocation(
            DataForTesting.dummyToken,
        )

        val actualResponse = result.last()

        assertNotNull(actualResponse.data)
        assertTrue(actualResponse is Resource.Success)
        assertEquals(
            expectedResponse.data?.size,
            actualResponse.data?.size
        )
        assertEquals(
            expectedResponse.data?.first(),
            actualResponse.data?.first()
        )
    }

    @Test
    fun `upload new story should return success`() = runTest {
        val expectedResponse = Resource.Success(DataForTesting.dummyUploadStory())
        val result = storiesRepository.uploadNewStory(
            DataForTesting.dummyToken,
            DataForTesting.dummyPhoto,
            DataForTesting.dummyDescription,
            null, null
        )

        val actualResponse = result.last()

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Resource.Success)
        assertEquals(
            expectedResponse.data.toString(),
            actualResponse.data.toString()
        )
    }
}