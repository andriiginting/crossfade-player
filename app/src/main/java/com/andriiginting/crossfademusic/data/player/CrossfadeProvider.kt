package com.andriiginting.crossfademusic.data.player

import android.content.Context
import android.net.Uri
import com.andriiginting.crossfademusic.data.CrossFadeClippingData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

object CrossFadeProvider {

    fun crossFadeInstance(context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    fun provideHlsMediaSource(url: String): HlsMediaSource {
        return HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }

    fun concatenatingMediaSource(
        pairs: Pair<CrossFadeClippingData, CrossFadeClippingData>
    ): ConcatenatingMediaSource {
        val first = ClippingMediaSource(
            pairs.first.mediaSource,
            pairs.first.start,
            pairs.first.end
        )

        val second = ClippingMediaSource(
            pairs.second.mediaSource,
            pairs.second.start,
            pairs.second.end
        )

        return ConcatenatingMediaSource(true, first, second)
    }
}