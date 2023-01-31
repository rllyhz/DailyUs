package id.rllyhz.dailyus.presentation.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.presentation.adapter.StoriesPagingAdapter
import id.rllyhz.dailyus.utils.CoroutinesTestRule
import id.rllyhz.dailyus.utils.DataForTesting
import id.rllyhz.dailyus.utils.PagedTestDataSource
import id.rllyhz.dailyus.utils.getOrWaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var preferences: AuthPreferences

    @Mock
    private lateinit var storiesDao: StoriesDao

    @Mock
    private lateinit var storiesRepository: DailyStoriesRepository

    @Test
    fun `when get stories should not null and return data`() = runTest {
        val stories = DataForTesting.dummyStories()
        val data = PagedTestDataSource.snapshot(stories)
        val expectedStories = flow { emit(data) }

        Mockito.`when`(storiesRepository.getPagingStories(DataForTesting.dummyToken))
            .thenReturn(expectedStories)

        val viewModel = MainViewModel(preferences, storiesRepository, storiesDao)

        val actualStories = viewModel.loadPagingStories(DataForTesting.dummyToken)
            .asLiveData()
            .getOrWaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesPagingAdapter.DIFF_UTIL,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        ).also { it.submitData(actualStories) }

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(stories.size, differ.snapshot().size)
        Assert.assertEquals(stories[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when get stories with no data should has size 0`() = runTest {
        val data = PagedTestDataSource.snapshot(emptyList())
        val expectedStories = flow { emit(data) }

        Mockito.`when`(storiesRepository.getPagingStories(DataForTesting.dummyToken))
            .thenReturn(expectedStories)

        val viewModel = MainViewModel(preferences, storiesRepository, storiesDao)

        val actualStories = viewModel.loadPagingStories(DataForTesting.dummyToken)
            .asLiveData()
            .getOrWaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesPagingAdapter.DIFF_UTIL,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        ).also { it.submitData(actualStories) }

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}