package id.rllyhz.dailyus.presentation.ui.auth

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@LargeTest
@HiltAndroidTest
class AuthActivityTest {

    @get:Rule
    var rule = RuleChain.outerRule(HiltAndroidRule(this))
        .around(ActivityScenarioRule(AuthActivity::class.java))

    @Test
    fun launchAuthActivity() {
        //
    }
}