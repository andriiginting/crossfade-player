package com.andriiginting.crossfademusic.data

import io.reactivex.Single
import javax.inject.Inject

interface CrossFadeRepository {
    fun getPlaylist(): Single<CrossFadeMusicResponse>
}

class CrossFadeRepositoryImpl @Inject constructor(private val service: CrossFadeService) : CrossFadeRepository {
    override fun getPlaylist(): Single<CrossFadeMusicResponse> {
        return service.getPlaylist()
    }
}