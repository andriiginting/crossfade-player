package com.andriiginting.crossfademusic.domain

import androidx.core.net.toUri
import com.andriiginting.crossfademusic.data.player.CrossFadeProvider
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource

data class MusicPlaylist(
    val id: Long,
    val track: String,
    val musicUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false
) {
    companion object {
        fun default(): MusicPlaylist {
            return MusicPlaylist(0L, "", "", "", "")
        }
    }

    fun getMediaSource(): MediaSource {
        val uri = musicUrl.toUri()
        return ProgressiveMediaSource.Factory(CrossFadeProvider.provideHttpDataSource())
            .createMediaSource(MediaItem.fromUri(uri))
    }
}
