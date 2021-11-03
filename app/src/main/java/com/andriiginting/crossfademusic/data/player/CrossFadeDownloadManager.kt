package com.andriiginting.crossfademusic.data.player

import android.app.Notification
import android.content.Context
import android.util.Log
import com.andriiginting.crossfademusic.R
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

class CrossFadeDownloadManager :
    DownloadService(2021, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL) {

    private lateinit var notificationHelper: DownloadNotificationHelper
    private val context: Context = this

    override fun onCreate() {
        super.onCreate()
        notificationHelper = DownloadNotificationHelper(
            this,
            context.resources.getString(R.string.app_name)
        )
    }

    override fun getDownloadManager(): DownloadManager {
        val dataSource = DefaultHttpDataSourceFactory(
            Util.getUserAgent(context, context.resources.getString(R.string.app_name)),
            null,
            30 * 1000, 30 * 1000,
            true
        )

        return DownloadManager(
            context,
            ExoDatabaseProvider(context),
            CrossFadeCache.getInstance(context),
            dataSource,
            Executors.newFixedThreadPool(2)
        ).apply {
            maxParallelDownloads = 3
            addListener(object : DownloadManager.Listener {
                override fun onDownloadsPausedChanged(
                    downloadManager: DownloadManager,
                    downloadsPaused: Boolean
                ) {
                    super.onDownloadsPausedChanged(downloadManager, downloadsPaused)
                    if (downloadsPaused.not()) {
                        Log.d("crossfade-download", "file still downloading")
                    }
                }
            })
        }
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        return notificationHelper.buildDownloadCompletedNotification(
            context,
            R.drawable.exo_notification_small_icon,
            null,
            "Downloading"
        )
    }

    companion object {
        fun downloadMediaItem(
            context: Context,
            items: MusicPlaylist,
            onPrepare: () -> Unit,
            onError: (e: IOException) -> Unit
        ) {
            val mediaItem = fromUri(items.musicUrl)
            val downloadHelper = DownloadHelper.forMediaItem(context, mediaItem)
            downloadHelper.prepare(object : DownloadHelper.Callback {
                override fun onPrepared(helper: DownloadHelper) {
                    Log.e("feeds-download", "onprepared download")
                    onPrepare()
                    val json = JSONObject()
                    json.put("url", items.musicUrl)
                    json.put("id", items.id)
                    json.put("coverUrl", items.coverUrl)
                    json.put("artist", items.artist)
                    json.put("track", items.track)

                    val download = helper.getDownloadRequest(
                        items.musicUrl,
                        Util.getUtf8Bytes(json.toString())
                    )
                    sendAddDownload(
                        context,
                        CrossFadeDownloadManager::class.java,
                        download,
                        true
                    )
                }

                override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                    Log.e("feeds-download", "${e.message}")
                    onError(e)
                }

            })
        }

        fun getDownloadedItem(): MutableList<MusicPlaylist> {
            val downloadedVideo = mutableListOf<MusicPlaylist>()
            val cursor = CrossFadeDownloadManager().downloadManager.downloadIndex.getDownloads()

            if (cursor.moveToFirst()) {
                do {
                    val jsonString = Util.fromUtf8Bytes(cursor.download.request.data)
                    val jsonObject = JSONObject(jsonString)
                    val uri = cursor.download.request.uri

                    downloadedVideo.add(
                        MusicPlaylist(
                            id = jsonObject.getLong("id"),
                            musicUrl = uri.toString(),
                            coverUrl = jsonObject.getString("coverUrl"),
                            track = jsonObject.getString("track"),
                            artist = jsonObject.getString("artist")
                        )
                    )
                } while (cursor.moveToNext())
            }

            return downloadedVideo
        }
    }
}
