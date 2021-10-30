package com.andriiginting.crossfademusic.player

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer

object CrossFadeProvider {

    fun crossFadeInstance(context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }
}