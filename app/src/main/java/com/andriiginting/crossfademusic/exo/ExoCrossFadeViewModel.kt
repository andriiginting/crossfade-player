package com.andriiginting.crossfademusic.exo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andriiginting.crossfademusic.domain.CrossFadeUseCase
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ExoCrossFadeViewModel @Inject constructor(
    private val useCase: CrossFadeUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _state = MutableLiveData<ExoCrossFadeViewState>()
    val state: LiveData<ExoCrossFadeViewState> get() = _state

    var currentPosition = -1
    private val playlist = mutableListOf<MusicPlaylist>()

    fun initPlaylist() {
        useCase.getPlaylist()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                playlist.addAll(it)
                _state.value = ExoCrossFadeViewState.ExoPlaylist(it)
                _state.value = ExoCrossFadeViewState.ExoCurrentSong(it.first())
                _state.value =
                    ExoCrossFadeViewState.ExoFooterView(
                        it.first(),
                        it[1]
                    )
            }, {
                Log.e("ExoCrossFade", it.message, it)
                _state.value = ExoCrossFadeViewState.ExoPlaylistError
            })
            .let(compositeDisposable::add)
    }

    fun onPlay(isPlaying: Boolean) {
        if (!isPlaying) {
            _state.value = ExoCrossFadeViewState.ExoTrackPlaying
        } else {
            _state.value = ExoCrossFadeViewState.ExoTrackStopped
        }
    }

    fun getCurrentSong() {
        if (playlist.isNotEmpty()) {
            _state.value = ExoCrossFadeViewState.ExoCurrentSong(playlist[currentPosition])
        }
    }

    fun updateCurrentPlaying(data: MusicPlaylist) {
        currentPosition = playlist.indexOf(data)
    }

    fun getPreviousSong() {
        currentPosition = if ((currentPosition - 1) < 0) playlist.lastIndex else currentPosition - 1
        _state.value = ExoCrossFadeViewState.ExoPreviousSong(playlist[currentPosition])
        _state.value = ExoCrossFadeViewState.ExoFooterView(
            playlist[currentPosition], playlist[currentPosition+1]
        )
    }

    fun getNextSong() {
        if (playlist.isEmpty()) {
            return
        }

        currentPosition = if ((currentPosition + 1) == playlist.size) {
            0
        } else {
            currentPosition + 1
        }

        _state.value = ExoCrossFadeViewState.ExoNextSong(playlist[currentPosition])
        _state.value = ExoCrossFadeViewState.ExoFooterView(
            playlist[currentPosition], playlist[currentPosition+1]
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

sealed class ExoCrossFadeViewState {
    data class ExoPlaylist(val playlist: List<MusicPlaylist>) : ExoCrossFadeViewState()
    data class ExoNextSong(val song: MusicPlaylist) : ExoCrossFadeViewState()
    data class ExoPreviousSong(val song: MusicPlaylist) : ExoCrossFadeViewState()
    data class ExoCurrentSong(val song: MusicPlaylist) : ExoCrossFadeViewState()
    data class ExoFooterView(
        val mainSong: MusicPlaylist,
        val suggestion: MusicPlaylist
    ) : ExoCrossFadeViewState()

    object ExoPlaylistError : ExoCrossFadeViewState()
    object ExoPlaylistLoading : ExoCrossFadeViewState()

    object ExoTrackPlaying : ExoCrossFadeViewState()
    object ExoTrackStopped : ExoCrossFadeViewState()
}