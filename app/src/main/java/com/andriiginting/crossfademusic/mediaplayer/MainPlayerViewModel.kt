package com.andriiginting.crossfademusic.mediaplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andriiginting.crossfademusic.R

class MainPlayerViewModel : ViewModel() {
    private val playlist = mutableListOf<CrossfadeDataModel>()
    private var currentPosition = 0

    private val _state = MutableLiveData<CrossFadeViewState>()
    val state: LiveData<CrossFadeViewState> get() = _state

    init {
        populateData()
    }

    fun onViewCreated() {
        _state.value = CrossFadeViewState.PopulateData(playlist.first())
    }

    fun onPlay(isAudioPlayed: Boolean) {
        val nextPosition = currentPosition + 1
        if (isAudioPlayed) {
            _state.value = CrossFadeViewState.PrepareNextSong(nextPosition)
            _state.value = CrossFadeViewState.Played(playlist[currentPosition])
        } else {
            onStop()
        }
    }

    private fun onStop() {
        _state.value = CrossFadeViewState.Stopped
    }

    fun onNextSong() {
        if (currentPosition > 0 && currentPosition < playlist.size) {
            _state.value = CrossFadeViewState.PrepareNextSong(currentPosition + 1)
        }
    }

    fun onPreviousSong() {
        if (currentPosition > 0) {
            _state.value = CrossFadeViewState.PrepareNextSong(currentPosition - 1)
        }
    }

    fun onDestroy() {
        _state.value = CrossFadeViewState.Released
    }

    fun getPlaylist(): List<CrossfadeDataModel> = playlist

    private fun populateData() {
        CrossfadeDataModel(
            title = "1st Title",
            artist = "1st Artist",
            cover = "https://source.unsplash.com/weekly?music",
            track = R.raw.music_sample_1,
            trackUrl = "https://www.bensound.com/bensound-music/bensound-highoctane.mp3",
            isTrackPlaying = true
        ).let(playlist::add)

        CrossfadeDataModel(
            title = "2nd Title",
            artist = "2nd Artist",
            cover = "https://source.unsplash.com/weekly?music",
            trackUrl = "https://www.bensound.com/bensound-music/bensound-goinghigher.mp3",
            track = R.raw.music_sample_2
        ).let(playlist::add)

        CrossfadeDataModel(
            title = "3rd Title",
            artist = "3rd Artist",
            cover = "https://source.unsplash.com/weekly?music",
            trackUrl = "https://www.bensound.com/bensound-music/bensound-punky.mp3",
            track = R.raw.music_sample_3
        ).let(playlist::add)

        CrossfadeDataModel(
            title = "3rd Title",
            artist = "3rd Artist",
            cover = "https://source.unsplash.com/weekly?music",
            trackUrl = "https://www.free-stock-music.com/music/punch-deck-music-to-wear-fingerless-gloves-to.mp3",
            track = R.raw.music_sample_3
        ).let(playlist::add)
    }
}

sealed class CrossFadeViewState {
    object Stopped : CrossFadeViewState()
    object Released : CrossFadeViewState()

    data class PrepareNextSong(val position: Int) : CrossFadeViewState()
    data class Played(val data: CrossfadeDataModel) : CrossFadeViewState()
    data class PopulateData(val data: CrossfadeDataModel) : CrossFadeViewState()
}
