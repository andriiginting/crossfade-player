package com.andriiginting.crossfademusic.data

import com.google.gson.annotations.SerializedName

data class CrossFadeMusicResponse(
    @SerializedName("musics") val playlist: List<MusicResponse>
)

data class MusicResponse(
    @SerializedName("track") val track: String,
    @SerializedName("streamUrl") val streamUrl: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("cover") val coverUrl: String
)
