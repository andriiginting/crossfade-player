package com.andriiginting.crossfademusic.di

import com.andriiginting.crossfademusic.data.CrossFadeRepository
import com.andriiginting.crossfademusic.data.CrossFadeRepositoryImpl
import com.andriiginting.crossfademusic.data.CrossFadeService
import com.andriiginting.crossfademusic.domain.CrossFadeUseCase
import com.andriiginting.crossfademusic.domain.CrossFadeUseCaseImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}
