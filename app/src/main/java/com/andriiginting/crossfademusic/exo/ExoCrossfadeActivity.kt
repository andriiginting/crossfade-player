package com.andriiginting.crossfademusic.exo

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.andriiginting.crossfademusic.R
import com.andriiginting.crossfademusic.databinding.ActivityExoCrossfadeBinding
import com.andriiginting.crossfademusic.domain.MusicPlaylist
import com.andriiginting.crossfademusic.util.Constant
import com.andriiginting.crossfademusic.util.loadImage
import com.andriiginting.crossfademusic.util.setTransparentSystemBar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ExoCrossfadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExoCrossfadeBinding

    private val viewModel: ExoCrossFadeViewModel by viewModels()

    private var crossFadeDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExoCrossfadeBinding.inflate(layoutInflater)


        setContentView(binding.root)
        observePlaylist()
        setTransparentSystemBar()
        bindView()
        observeExtras()

        viewModel.setupMusicPlayer(crossFadeDuration)
    }

    private fun bindView() {

        binding.ivPlayPause.setOnClickListener {
            viewModel.onPlayOrStop()
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
                viewModel.seekTo(
                    (seekBar.progress * 1000).toLong()
                )
            }

        })
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

                ExoCrossFadeViewState.ExoPlaylistLoading -> {
                    //do nothing
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
                is ExoCrossFadeViewState.ExoPlaylist -> {

                }
                is ExoCrossFadeViewState.ExoTrackPaused -> {
                    toggleControlButton(false)
                }
                is ExoCrossFadeViewState.ExoTrackPlaying -> {
                    toggleControlButton(true)
                }
                is ExoCrossFadeViewState.ExoTrackStopped -> {
                    toggleControlButton(false)
                }
            }
        })
    }

    private fun bindSelectedTrack(data: MusicPlaylist) {
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
            tvNextSongHeader.text = getString(R.string.next_from, suggestion.artist)
            tvNextSongTitle.text = getString(R.string.similar_to, mainSong.track)
            ivNextSong.loadImage(suggestion.coverUrl)
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
    }
}