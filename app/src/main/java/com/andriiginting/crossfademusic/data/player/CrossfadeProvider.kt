package com.andriiginting.crossfademusic.data.player

import android.content.Context
import android.net.Uri
import com.andriiginting.crossfademusic.data.CrossFadeClippingData
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

object CrossFadeProvider {

    fun crossFadeInstance(context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setAudioAttributes(getAudioAttributes(), true)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(provideCacheFactory(context))
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    private fun provideCacheFactory(context: Context) = CacheDataSource.Factory()
        .setCache(CrossFadeCache.getInstance(context))
        .setUpstreamDataSourceFactory(provideHttpDataSource())
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private fun provideHttpDataSource() = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)

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

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

    }
}