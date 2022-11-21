package id.rllyhz.dailyus.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.local.db.DailyStoriesDatabase
import javax.inject.Inject

@AndroidEntryPoint
class StoriesBannerWidgetService : RemoteViewsService() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    @Inject
    lateinit var storiesDB: DailyStoriesDatabase

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StoriesBannerRemoteViewsFactory(
            this.application,
            authPreferences,
            storiesDB.getStoriesDao()
        )
}