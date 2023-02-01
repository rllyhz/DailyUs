package id.rllyhz.dailyus.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import kotlinx.coroutines.runBlocking

internal class StoriesBannerRemoteViewsFactory(
    private val context: Context,
    private val authPreferences: AuthPreferences,
    private val storiesDao: StoriesDao
) : RemoteViewsService.RemoteViewsFactory {

    private var stories = listOf<StoryEntity>()

    private fun loadData() {
        runBlocking {
            val isAlreadyLoggedIn = authPreferences.isAlreadyLoggedIn()

            if (isAlreadyLoggedIn) {
                val storiesInDb = storiesDao.getStories()
                stories = storiesInDb

                println(storiesInDb)
            }
        }
    }

    override fun onCreate() {
        loadData()
    }

    override fun onDestroy() {
        //
    }

    override fun onDataSetChanged() {
        //
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_stories_banner_item).apply {
            val story = stories[position]

            try {
                val bitmap: Bitmap = Glide.with(context.applicationContext)
                    .asBitmap()
                    .load(story.photoUrl)
                    .submit()
                    .get()

                setImageViewBitmap(R.id.widget_stories_banner_item_iv_photo, bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val extras = bundleOf(
                StoriesBannerWidget.EXTRA_ITEM to position
            )

            Intent().putExtras(extras).also {
                setOnClickFillInIntent(R.id.widget_stories_banner_item_iv_photo, it)
            }
        }

    override fun getLoadingView(): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_stories_banner_loading)

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}