package id.rllyhz.dailyus.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import id.rllyhz.dailyus.R

class StoriesBannerWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        intent?.action?.let { action ->
            when (action) {
                TOAST_ACTION -> {
                    val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
                    val message = context?.resources?.getString(
                        R.string.widget_stories_banner_item_clicked_message,
                        viewIndex + 1
                    )

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val TOAST_ACTION = "id.rllyhz.dailyus.presentation.widget.TOAST_ACTION"
        const val EXTRA_ITEM = "id.rllyhz.dailyus.presentation.widget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StoriesBannerWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.widget_stories_banner).apply {
                setRemoteAdapter(R.id.widget_stories_banner_stack_view, intent)
                setEmptyView(
                    R.id.widget_stories_banner_stack_view,
                    R.id.widget_stories_banner_tv_message
                )
            }

            val toastIntent = Intent(context, StoriesBannerWidget::class.java).apply {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )

            views.setPendingIntentTemplate(
                R.id.widget_stories_banner_stack_view,
                toastPendingIntent
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}