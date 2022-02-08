package com.andriiginting.crossfademusic.data.player

import android.content.Context
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import com.andriiginting.crossfademusic.exo.ExoCrossFadeViewState
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlin.math.log

interface PlayerController : BaseMusicPlayer {
    var volumeController: Float

    fun play(isPlayWhenReady: Boolean, song: MusicPlaylist)
    fun pause()
    fun resume()
    fun togglePlayback(callback: (states: ExoCrossFadeViewState) -> Unit)
    fun stop(callback: (states: ExoCrossFadeViewState.ExoTrackStopped) -> Unit)
    fun nextSong(callback: (states: ExoCrossFadeViewState.ExoNextSong) -> Unit)
    fun previousSong(callback: (states: ExoCrossFadeViewState.ExoPreviousSong) -> Unit)
    fun seekTo(progress: Long)
    fun isPlaying(): Boolean
    fun release()
}

interface BaseMusicPlayer {
    var exoPlayer: SimpleExoPlayer?
    var secondExoPlayer: SimpleExoPlayer?

    fun populatePlaylist(playlists: List<MusicPlaylist>)
    fun init(crossfade: Int)
}

class PlayerControllerImpl(private val context: Context) : PlayerController {

    private var currentListenerId = ""
    private var isPlaying = false
    private var exoPlayerSong = MusicPlaylist.default()
    private var crossfade = 0

    //songId of song which is prepared in nextExoPlayer
    private var secondExoPlayerSong = MusicPlaylist.default()

    private var playlist = mutableListOf<MusicPlaylist>()

    private var playlistIndex = 0
    private val currentSong: MusicPlaylist
        get() {
            return when {
                playlist.size > playlistIndex && playlistIndex >= 0 -> {
                    playlist[playlistIndex]
                }
                else -> {
                    MusicPlaylist.default()
                }
            }
        }

    private var duration: Long = 0
        set(newLong) {
            val changed = newLong != field

            field = if (newLong > 0) {
                newLong
            } else {
                0
            }

            if (changed) {
                setSongMetadata()

                //set callback to get seekbar position
                //currentPositionChangedCallback()
            }
        }

    private var currentPosition:Long = 0
        set(newLong) {
            val changed = newLong != field

            field = newLong

            if(changed){
//                currentPositionChangedCallback()
            }
        }

    private val mediaSession by lazy { CrossFadeProvider.provideMediaSession(context) }

    override var exoPlayer: SimpleExoPlayer? = null
        set(value) {
            currentListenerId = ""

            field?.playWhenReady = false
            field?.release()

            field = value
        }

    override var secondExoPlayer: SimpleExoPlayer? = null
        set(value) {
            secondExoPlayer?.playWhenReady = false

            field = value
        }

    override fun populatePlaylist(playlists: List<MusicPlaylist>) {
        this.playlist.addAll(playlists)
    }

    override fun init(crossfade: Int) {
        this.crossfade = crossfade
        initCrossFade()
    }

    override var volumeController: Float = 0.0f
        set(value) {
            field = if (value > 1) {
                exoPlayer?.audioComponent?.volume = 1F
                1F
            } else {
                exoPlayer?.audioComponent?.volume = value
                value
            }
        }

    override fun play(isPlayWhenReady: Boolean, song: MusicPlaylist) {
        if (exoPlayerSong != song) {
            if (secondExoPlayerSong != song) {
                prepareSong(song)
            } else {
                preparePlayer(isPlayWhenReady)
            }
        } else {
            preparePlayer(isPlayWhenReady)
        }
    }

    override fun pause() {
        exoPlayer?.playWhenReady = false
        isPlaying = false
    }

    override fun resume() {
        play(true, currentSong)
    }

    override fun togglePlayback(callback: (states: ExoCrossFadeViewState) -> Unit) {
        if (isPlaying) {
            pause()
            callback(ExoCrossFadeViewState.ExoTrackPaused)
        } else {
            resume()
            callback(ExoCrossFadeViewState.ExoTrackPlaying)
        }
    }

    override fun stop(callback: (states: ExoCrossFadeViewState.ExoTrackStopped) -> Unit) {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.stop()
            callback(ExoCrossFadeViewState.ExoTrackStopped)
        }
        isPlaying = false
    }

    override fun nextSong(callback: (states: ExoCrossFadeViewState.ExoNextSong) -> Unit) {
        val nextSong = nextSong()
        play(true, nextSong)
        callback(ExoCrossFadeViewState.ExoNextSong(nextSong))
    }

    override fun previousSong(callback: (states: ExoCrossFadeViewState.ExoPreviousSong) -> Unit) {
        val prevSong = previousSong()
        play(true, prevSong)
        callback(ExoCrossFadeViewState.ExoPreviousSong(prevSong))
    }

    override fun seekTo(progress: Long) {
        exoPlayer?.seekTo(progress)
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    override fun release() {
        exoPlayer?.release()
        secondExoPlayer?.release()
    }

    private fun preparePlayer(
        isPlayWhenReady: Boolean,
        progress: Long = 0L
    ) {
        if (secondExoPlayer?.isPlaying == true) {
            exoPlayer = secondExoPlayer
        }

        seekPosition(progress)
        resetSecondaryPlayer()

        exoPlayer?.apply {
            volume = volumeController
            playWhenReady = isPlayWhenReady
        }
    }

    private fun seekPosition(progress: Long) {
        if (progress != 0L) {
            exoPlayer?.seekTo(progress)
        } else {
            exoPlayer?.seekTo(0)
        }
    }

    private fun resetSecondaryPlayer() {
        secondExoPlayer = CrossFadeProvider.crossFadeInstance(context)
    }

    private fun prepareSong(song: MusicPlaylist) {
        if (secondExoPlayerSong != song) {
            resetSecondaryPlayer()
        }

        secondExoPlayer?.apply {
            setMediaSource(song.getMediaSource())
            prepare()
            secondExoPlayerSong = song
        }
    }

    private fun nextSong(): MusicPlaylist {
        if (playlist.size >= playlistIndex) {
            return try {
                playlistIndex++
                playlist[playlistIndex]
            } catch (e: MusicPlaylistIndexOutOfBound) {
                Log.e("crossfade-player", e.message.orEmpty())
                return MusicPlaylist.default()
            }
        } else {
            return MusicPlaylist.default()
        }
    }

    private fun previousSong(): MusicPlaylist {
        return kotlin.runCatching {
            val song = playlist[playlistIndex - 1]
            playlistIndex--
            song
        }.getOrDefault(MusicPlaylist.default())
    }

    private fun setSongMetadata() {
        kotlin.runCatching {
            val currentSong = currentSong
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_ARTIST, currentSong.artist)
                .putString(MediaMetadata.METADATA_KEY_TITLE, currentSong.track)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, duration)

            mediaSession.setMetadata(metadata.build())
        }
    }

    private fun initCrossFade() {
        if (duration >= 1 && currentPosition >= 1) {
            val timeRemaining = duration - currentPosition

            if (timeRemaining < duration / 2 && exoPlayer?.isPlaying == true) {
                prepareSong(nextSong())
            }

            if (timeRemaining in 200 until crossfade && exoPlayer?.isPlaying == true) {
                val volume = 1 - log(
                    100 - ((crossfade.toFloat() - timeRemaining) / crossfade * 100),
                    100.toFloat()
                )

                val highVolume = volume * volumeController
                val lowVolume = volume - highVolume

                if (highVolume > 0F && highVolume.isFinite()) {
                    secondExoPlayer?.audioComponent?.volume = highVolume
                }

                if (lowVolume > 0F && lowVolume.isFinite()) {
                    exoPlayer?.audioComponent?.volume = lowVolume
                }

                secondExoPlayer?.playWhenReady = true
            } else if(exoPlayer?.isPlaying == true) {
                secondExoPlayer?.playWhenReady = false
            }
        } else {
            secondExoPlayer?.playWhenReady = false
        }
    }
}