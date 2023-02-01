package id.rllyhz.dailyus.presentation.ui.auth

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class SplashFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun launchSplashFragment_Success() {
        launchFragmentInContainer<SplashFragment>()
    }
}