package com.andriiginting.crossfademusic.di

import com.andriiginting.crossfademusic.data.CrossFadeRepository
import com.andriiginting.crossfademusic.data.CrossFadeRepositoryImpl
import com.andriiginting.crossfademusic.domain.CrossFadeUseCase
import com.andriiginting.crossfademusic.domain.CrossFadeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExoCrossFadeModule {

    @Binds
    abstract fun bindRepository(repositoryImpl: CrossFadeRepositoryImpl): CrossFadeRepository

    @Binds
    abstract fun bindUseCase(useCaseImpl: CrossFadeUseCaseImpl): CrossFadeUseCase
}