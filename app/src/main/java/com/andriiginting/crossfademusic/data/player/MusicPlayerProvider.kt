package com.andriiginting.crossfademusic.data.player

import android.content.Context
import android.content.Intent
import android.util.Log
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import com.google.android.exoplayer2.SimpleExoPlayer


interface MusicPlayerProvider {
    fun setupPlayer(crossFade: Int = 0)
    fun populatePlaylist(playlists: List<MusicPlaylist>)
    fun isPlaying(): Boolean
    fun release()
}

class MusicPlayerProviderImpl(
    private val context: Context,
    private val controller: PlayerController
) : MusicPlayerProvider {

    private var playlists = mutableListOf<MusicPlaylist>()

    override fun setupPlayer(crossFade: Int) {
        controller.secondExoPlayer = CrossFadeProvider.crossFadeInstance(context = context)
        Log.d("music-provider", "setting up provider")
    }

    override fun populatePlaylist(playlists: List<MusicPlaylist>) {
        this.playlists.addAll(playlists)
        onPreloadingStarted(playlists)
    }

    override fun isPlaying(): Boolean {
        return controller.isPlaying()
    }

    override fun release() {
        controller.release()
    }

    private fun onPreloadingStarted(list: List<MusicPlaylist>) {
        Intent(context, PreloadingCache::class.java).apply {
            putStringArrayListExtra(PreloadingCache.KEY_LIST, ArrayList(list.map { it.musicUrl }))
        }.also(context::startService)
    }
}