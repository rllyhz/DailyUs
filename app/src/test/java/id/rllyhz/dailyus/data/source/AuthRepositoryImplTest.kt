package id.rllyhz.dailyus.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import id.rllyhz.dailyus.data.source.remote.FakeAuthApi
import id.rllyhz.dailyus.data.source.remote.network.DailyUsAuthApiService
import id.rllyhz.dailyus.utils.CoroutinesTestRule
import id.rllyhz.dailyus.utils.DataForTesting
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class AuthRepositoryImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var authApi: DailyUsAuthApiService
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        authApi = FakeAuthApi()
        authRepository = AuthRepositoryImpl(authApi)
    }

    @Test
    fun `login registered user`() = runTest {
        val expectedResponse = Resource.Success(DataForTesting.dummyLogin())
        val result = authRepository.loginUser(
            DataForTesting.dummyEmail, DataForTesting.dummyPassword
        )

        val actualResponse = result.last()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Resource.Success)
        Assert.assertEquals(
            expectedResponse.data?.loginResult.toString(),
            actualResponse.data?.loginResult.toString()
        )
    }

    @Test
    fun `register new user`() = runTest {
        val expectedResponse = Resource.Success(DataForTesting.dummyRegister())
        val result = authRepository.registerNewUser(
            DataForTesting.dummyName, DataForTesting.dummyEmail, DataForTesting.dummyPassword
        )

        val actualResponse = result.last()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Resource.Success)
        Assert.assertEquals(
            expectedResponse.data.toString(),
            actualResponse.data.toString()
        )
    }
}