package com.andriiginting.crossfademusic.domain

data class MusicPlaylist(
    val id: Long,
    val track: String,
    val musicUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false
)
