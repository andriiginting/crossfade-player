package com.andriiginting.crossfademusic

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.andriiginting.crossfademusic.databinding.ActivityMainBinding
import com.andriiginting.crossfademusic.util.loadImage
import com.andriiginting.crossfademusic.util.setTransparentSystemBar
import com.google.android.material.slider.LabelFormatter
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var bindingInst: ActivityMainBinding? = null
    private val binding get() = bindingInst!!

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    private val viewModel by lazy {
        ViewModelProvider(this).get(MainPlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.music_sample_1)
        viewModel.onViewCreated()
        handlePlayerController()
        observePlayer()
        setTransparentSystemBar()

        binding.fabClosePlayer.setOnClickListener {
            onBackPressed()
        }
    }

    private fun handlePlayerController() {
        with(binding) {
            ivPlayPause.setOnClickListener {
                viewModel.onPlay(!isPlaying)
            }

            ivNext.setOnClickListener {
                viewModel.onNextSong()
            }

            ivPrevious.setOnClickListener {
                viewModel.onPreviousSong()
            }
        }
    }

    private fun observePlayer() {
        viewModel.state.observe(this, { state ->
            when (state) {
                is CrossFadeViewState.Released -> {
                    mediaPlayer.release()
                }
                is CrossFadeViewState.Played -> {
                    isPlaying = true
                    bindTrackDetail(state.data)
                    toggleControlButton(isPlaying)
                    mediaPlayer.apply {
                        if (currentPosition != 0) {
                            seekTo(currentPosition)
                        }
                        start()
                        startAudioProgress(this)
                    }
                }
                is CrossFadeViewState.PrepareNextSong -> {
                    val data = viewModel.getPlaylist()[state.position]
                    mediaPlayer.setNextMediaPlayer(MediaPlayer.create(this, data.track))
                }
                is CrossFadeViewState.Stopped -> {
                    isPlaying = false
                    mediaPlayer.pause()
                    toggleControlButton(isPlaying)
                }

                is CrossFadeViewState.PopulateData -> {
                    bindTrackDetail(state.data)
                }

                else -> {
                    mediaPlayer.stop()
                }
            }
        })
    }

    private fun bindTrackDetail(data: CrossfadeDataModel) {
        binding.tvArtistName.text = data.artist
        binding.tvSongTitle.text = data.title
        binding.tvStartDuration.text = "00:00"

        val seconds = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.duration.toLong() / 1000 % 60)
        binding.tvStartEnded.text = formatTimeInMillisToString(mediaPlayer.duration.toLong())

        binding.sliderMusic.apply {
            valueTo = mediaPlayer.duration.toFloat()
            stepSize = seconds.toFloat()
            labelBehavior = LabelFormatter.LABEL_GONE
            addOnChangeListener { slider, value, fromUser ->
                binding.tvStartDuration.text = formatTimeInMillisToString(value.toLong())
                if (fromUser) {
                    mediaPlayer.seekTo(value.toInt())
                }
            }
        }
        binding.ivSongCover.loadImage(data.cover)
        binding.footerView.apply {
            ivNextSong.loadImage(data.cover)
            tvNextSongHeader.text = "NEXT FROM"
            tvNextSongTitle.text = "Similar to ${data.title}"
        }

    }

    private fun formatTimeInMillisToString(time: Long): String {
        var timeInMillis = time
        var sign = ""
        if (timeInMillis < 0) {
            sign = "-"
            timeInMillis = kotlin.math.abs(timeInMillis)
        }

        val minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1)
        val seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1)

        val formatted = StringBuilder(20)
        formatted.append(sign)
        formatted.append(String.format("%02d", minutes))
        formatted.append(String.format(":%02d", seconds))

        return try {
            String(formatted.toString().toByteArray(), Charset.forName("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            "00:00"
        }
    }

    private fun startAudioProgress(mediaPlayer: MediaPlayer) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                binding.sliderMusic.value = mediaPlayer.currentPosition.toFloat()
                handler.postDelayed(this, 1000)
            }
        }, 0)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        bindingInst = null
    }
}