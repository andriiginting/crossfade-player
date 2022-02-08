package com.andriiginting.crossfademusic.data.player

import android.content.Context
import android.media.session.MediaSession
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import com.andriiginting.crossfademusic.data.CrossFadeClippingData
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

object CrossFadeProvider {

    fun crossFadeInstance(context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setAudioAttributes(getAudioAttributes(), false)
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

    fun provideHttpDataSource() = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)

    fun provideMediaSession(context: Context): MediaSessionCompat {
        return MediaSessionCompat.fromMediaSession(
            context,
            MediaSession(context, "com.andriiginting.crossfademusic.data.player")
        )
    }

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

    }
}