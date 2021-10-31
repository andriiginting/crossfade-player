package com.andriiginting.crossfademusic.data

import io.reactivex.Single
import retrofit2.http.GET

interface CrossFadeService {
    @GET("v3/44010cb0-0019-4a76-97a4-9670e3ae4f88")
    fun getPlaylist(): Single<CrossFadeMusicResponse>
}