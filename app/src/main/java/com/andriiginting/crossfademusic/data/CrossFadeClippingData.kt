package com.andriiginting.crossfademusic.data

import com.google.android.exoplayer2.source.MediaSource

data class CrossFadeClippingData(
    val mediaSource: MediaSource,
    val start: Long,
    val end: Long
)
