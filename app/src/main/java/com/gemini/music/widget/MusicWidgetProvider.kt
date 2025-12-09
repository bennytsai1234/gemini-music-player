package com.gemini.music.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.gemini.music.R
import com.gemini.music.core.common.WidgetConstants

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == WidgetConstants.ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, MusicWidgetProvider::class.java))
            
            val isPlaying = intent.getBooleanExtra(WidgetConstants.EXTRA_IS_PLAYING, false)
            val title = intent.getStringExtra(WidgetConstants.EXTRA_TITLE) ?: "No Music"
            val artist = intent.getStringExtra(WidgetConstants.EXTRA_ARTIST) ?: "Select a song"
            val albumId = intent.getLongExtra(WidgetConstants.EXTRA_ALBUM_ID, -1L)

            for (appWidgetId in ids) {
                updateAppWidgetUI(context, appWidgetManager, appWidgetId, title, artist, isPlaying, albumId)
            }
        }
    }

    private fun updateAppWidgetUI(
        context: Context, 
        appWidgetManager: AppWidgetManager, 
        appWidgetId: Int,
        title: String,
        artist: String,
        isPlaying: Boolean,
        albumId: Long
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_music_player)
        
        views.setTextViewText(R.id.widget_title, title)
        views.setTextViewText(R.id.widget_artist, artist)
        
        // Play/Pause Icon
        views.setImageViewResource(
            R.id.widget_btn_play_pause, 
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )

        // Album Art (Basic Logic for now, loading actual bitmaps in Widget requires RemoteViews.setImageViewBitmap and content resolution which is heavy on main thread usually)
        // For simplicity, we use a placeholder or handle it if we passed a Bitmap. 
        // passing URI to ImageView in RemoteViews isn't directly supported for all URIs.
        // We will stick to metadata text and play state for phase 1.
        
        setupPendingIntents(context, views)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.widget_music_player)
    setupPendingIntents(context, views)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun setupPendingIntents(context: Context, views: RemoteViews) {
    // Open App
    val openAppIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    val openAppPendingIntent = PendingIntent.getActivity(
        context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    views.setOnClickPendingIntent(R.id.widget_album_art, openAppPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_title, openAppPendingIntent)

    // Media Controls - sending KeyEvents to MediaSession is the standard way
    views.setOnClickPendingIntent(R.id.widget_btn_prev, getMediaKeyPendingIntent(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS))
    views.setOnClickPendingIntent(R.id.widget_btn_play_pause, getMediaKeyPendingIntent(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
    views.setOnClickPendingIntent(R.id.widget_btn_next, getMediaKeyPendingIntent(context, KeyEvent.KEYCODE_MEDIA_NEXT))
}

@OptIn(UnstableApi::class)
private fun getMediaKeyPendingIntent(context: Context, keyCode: Int): PendingIntent {
    val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
    intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
    // We target the MediaSessionService. Since we know the class, we can target it implicitly via MediaButtonReceiver 
    // OR just broadcast it. MediaSessionService automatically handles ACTION_MEDIA_BUTTON.
    // However, to be safe, let's target the receiver if registered, or system. 
    // Android's way: sendBroadcast.
    
    // Better way for Media3/ExoPlayer:
    // With Media3, media buttons are handled if you set up the session correctly.
    // But widgets send broadcasts. Who receives it?
    // androidx.media3.session.MediaButtonReceiver
    
    intent.component = ComponentName(context, androidx.media3.session.MediaButtonReceiver::class.java)

    
    return PendingIntent.getBroadcast(
        context, 
        keyCode, 
        intent, 
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}
