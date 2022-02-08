package com.andriiginting.crossfademusic.di

import android.content.Context
import com.andriiginting.crossfademusic.data.CrossFadeService
import com.andriiginting.crossfademusic.data.player.MusicPlayerProvider
import com.andriiginting.crossfademusic.data.player.MusicPlayerProviderImpl
import com.andriiginting.crossfademusic.data.player.PlayerController
import com.andriiginting.crossfademusic.data.player.PlayerControllerImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CrossFadeModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("https://run.mocky.io/")
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideExoService(retrofit: Retrofit): CrossFadeService {
        return retrofit.create(CrossFadeService::class.java)
    }

    @Provides
    fun provideMusicController(@ApplicationContext context: Context): PlayerController {
        return PlayerControllerImpl(context)
    }

    @Provides
    fun provideMusicPlayer(
        @ApplicationContext context: Context,
        controller: PlayerController
    ): MusicPlayerProvider {
        return MusicPlayerProviderImpl(context, controller)
    }
}
