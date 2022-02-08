package com.andriiginting.crossfademusic.exo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andriiginting.crossfademusic.data.player.PlayerController
import com.andriiginting.crossfademusic.domain.CrossFadeUseCase
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ExoCrossFadeViewModel @Inject constructor(
    private val useCase: CrossFadeUseCase,
    private val musicController: PlayerController
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _state = MutableLiveData<ExoCrossFadeViewState>()
    val state: LiveData<ExoCrossFadeViewState> get() = _state

    init {
        initPlaylist()
    }

    fun setupMusicPlayer(crossfade: Int) = musicController.init(crossfade)

    private fun initPlaylist() {
        useCase.getPlaylist()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                musicController.populatePlaylist(it)
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

    fun onPlayOrStop() {
        musicController.togglePlayback { state ->
            Log.d("ExoCrossFade", "$state")
            _state.value = state
        }
    }

    fun seekTo(progress: Long) {
        musicController.seekTo(progress)
    }

    fun getPreviousSong() {
        musicController.previousSong { state ->
            _state.value = state
            Log.d("crossfade-player", "#prevSong: ${state.song}")
        }
    }

    fun getNextSong() {
        musicController.nextSong { state ->
            Log.d("crossfade-player", "#nextSong: ${state.song}")
            _state.value = state
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicController.release()
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
    object ExoTrackPaused : ExoCrossFadeViewState()
}