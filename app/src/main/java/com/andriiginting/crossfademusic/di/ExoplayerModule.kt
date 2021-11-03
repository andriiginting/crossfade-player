package com.andriiginting.crossfademusic.di

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
object ExoplayerModule {

    private const val MAX_CACHE_SIZE = 100 * 1024 * 1024

    @Provides
    fun provideExoplayerInstance(@ApplicationContext  context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    @Provides
    fun provideCacheFactory(
        @Named("exoHttpDataSource") httpDataSource: DefaultHttpDataSource.Factory,
        @Named("exoCache") simpleCache: SimpleCache
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSource)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    @Provides
    @Named("exoHttpDataSource")
    fun provideHttpDataSource() = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)

    @Provides
    @Named("exoCache")
    fun provideCache(@ApplicationContext context: Context): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, "exoCrossFade"),
            LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE.toLong()),
            ExoDatabaseProvider(context)
        )
    }
}