package com.andriiginting.crossfademusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriiginting.crossfademusic.databinding.ActivityExoCrossfadeBinding
import com.andriiginting.crossfademusic.player.CrossFadeProvider
import com.google.android.exoplayer2.SimpleExoPlayer

class ExoCrossfadeActivity : AppCompatActivity() {

    private var bindingInst: ActivityExoCrossfadeBinding? = null
    private val binding get() = bindingInst!!

    private lateinit var exoplayerInstance: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityExoCrossfadeBinding.inflate(layoutInflater)

        exoplayerInstance = CrossFadeProvider.crossFadeInstance(this)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingInst = null
    }
}