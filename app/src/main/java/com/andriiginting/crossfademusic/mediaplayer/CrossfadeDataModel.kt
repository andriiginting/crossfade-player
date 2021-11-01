package com.andriiginting.crossfademusic.mediaplayer

data class CrossfadeDataModel(
    val title: String,
    val artist: String,
    val cover: String,
    val track: Int,
    val trackUrl: String,
    var isTrackPlaying: Boolean = false
)
