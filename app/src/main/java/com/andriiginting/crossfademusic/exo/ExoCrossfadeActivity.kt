package com.andriiginting.crossfademusic.exo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.andriiginting.crossfademusic.R
import com.andriiginting.crossfademusic.data.CrossFadeClippingData
import com.andriiginting.crossfademusic.data.player.CrossFadeProvider
import com.andriiginting.crossfademusic.databinding.ActivityExoCrossfadeBinding
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import com.andriiginting.crossfademusic.util.Constant
import com.andriiginting.crossfademusic.util.loadImage
import com.andriiginting.crossfademusic.util.setTransparentSystemBar
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ExoCrossfadeActivity : AppCompatActivity() {

    private var bindingInst: ActivityExoCrossfadeBinding? = null
    private val binding get() = bindingInst!!

    private lateinit var exoplayerInstance: SimpleExoPlayer
    private val viewModel: ExoCrossFadeViewModel by viewModels()

    private var crossFadeDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityExoCrossfadeBinding.inflate(layoutInflater)

        exoplayerInstance = CrossFadeProvider.crossFadeInstance(this)
        setContentView(binding.root)
        viewModel.initPlaylist()
        observePlaylist()
        setTransparentSystemBar()

        binding.ivPlayPause.setOnClickListener {
            viewModel.onPlay(exoplayerInstance.isPlaying)
        }

        binding.fabClosePlayer.setOnClickListener {
            onBackPressed()
        }

        binding.ivNext.setOnClickListener {
            viewModel.getNextSong()
        }

        binding.ivPrevious.setOnClickListener {
            viewModel.getPreviousSong()
        }

        binding.sliderMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                exoplayerInstance.seekTo((seekBar.progress * 1000).toLong())
            }

        })

        addPlayerListener()
        observeExtras()
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingInst = null
        exoplayerInstance.release()
    }

    override fun onStop() {
        super.onStop()
        exoplayerInstance.stop()
    }

    private fun observeExtras() {
        crossFadeDuration = intent.getIntExtra(
            Constant.CROSSFADE_DURATION_EXTRA,
            Constant.CROSSFADE_DURATION_DEFAULT
        )
    }

    private fun observePlaylist() {
        viewModel.state.observe(this, { states ->
            when (states) {
                is ExoCrossFadeViewState.ExoPlaylistError -> {
                    //do nothing
                }
                is ExoCrossFadeViewState.ExoPlaylist -> {
                    Log.d("ExoCrossFade", "${states.playlist}")
                    addPlaylistItem(states.playlist)
                }
                ExoCrossFadeViewState.ExoPlaylistLoading -> {
                    //do nothing
                }
                ExoCrossFadeViewState.ExoTrackPlaying -> {
                    exoplayerInstance.play()
                }
                ExoCrossFadeViewState.ExoTrackStopped -> {
                    exoplayerInstance.pause()
                }
                is ExoCrossFadeViewState.ExoNextSong -> {
                    bindSelectedTrack(states.song)
                }
                is ExoCrossFadeViewState.ExoPreviousSong -> {
                    bindSelectedTrack(states.song)
                }
                is ExoCrossFadeViewState.ExoCurrentSong -> {
                    bindSelectedTrack(states.song)
                }
                is ExoCrossFadeViewState.ExoFooterView -> {
                    bindFooterView(states.mainSong, states.suggestion)
                }
            }
        })
    }

    private fun bindSelectedTrack(data: MusicPlaylist) {
        playMusic(data)
        with(binding) {
            tvArtistName.text = data.artist
            tvSongTitle.text = data.track
            ivSongCover.loadImage(data.coverUrl)
        }
    }

    private fun bindFooterView(
        mainSong: MusicPlaylist,
        suggestion: MusicPlaylist
    ) {
        with(binding.footerView) {
            tvNextSongHeader.text = "NEXT FROM ${suggestion.artist}"
            tvNextSongTitle.text = "Similar to ${mainSong.track}"
            ivNextSong.loadImage(suggestion.coverUrl)
        }

    }

    private fun addPlayerListener() {
        exoplayerInstance.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                toggleControlButton(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                //update seekbar
                startAudioProgress()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                viewModel.currentPosition = (mediaItem?.mediaId ?: "0").toInt()
                viewModel.getCurrentSong()
            }
        })
    }

    private fun addPlaylistItem(playlist: List<MusicPlaylist>) {
        exoplayerInstance.apply {
            addMediaItems(
                playlist.map { music ->
                    MediaItem.Builder()
                        .setMediaId(music.id.toString())
                        .setUri(music.musicUrl)
                        .build()
                }
            )

            prepare()
        }
    }

    private fun toggleControlButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.ivPlayPause.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_pause_circle_24
                )
            )
        } else {
            binding.ivPlayPause.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_play_circle_24
                )
            )
        }
    }

    private fun startAudioProgress() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateSeekbar(
                    exoplayerInstance.duration,
                    exoplayerInstance.currentPosition
                )

                handler.postDelayed(this, 1000)
            }
        }, 0)
    }

    private fun updateSeekbar(duration: Long, position: Long) {
        val durations = if (duration >= 0) duration else 0

        binding.tvStartDuration.text = String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
            (position / 1000) % 60
        )
        binding.tvStartEnded.text = String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(durations, TimeUnit.MILLISECONDS),
            (durations / 1000) % 60
        )

        binding.sliderMusic.apply {
            max = (durations / 1000).toInt()
            progress = (position / 1000).toInt()
        }

        viewModel.autoNextSong(
            (position / 1000).toInt(),
            (durations / 1000).toInt(),
            crossFadeDuration)
    }

    private fun playMusic(data: MusicPlaylist) {
        viewModel.updateCurrentPlaying(data)
        exoplayerInstance.apply {
            seekTo(viewModel.currentPosition, 0)
            play()
        }
    }
}