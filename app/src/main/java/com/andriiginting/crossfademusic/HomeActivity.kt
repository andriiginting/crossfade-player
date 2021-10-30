package com.andriiginting.crossfademusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriiginting.crossfademusic.databinding.ActivityHomeBinding
import com.andriiginting.crossfademusic.util.Navigator

class HomeActivity : AppCompatActivity() {

    private var bindingInst: ActivityHomeBinding? = null
    private val binding get() = bindingInst!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExoplayer.setOnClickListener {
            Navigator.exoPlayerScreen(this)
                .also(::startActivity)
        }

        binding.btnMediaplayer.setOnClickListener {
            Navigator.mediaPlayerScreen(this)
                .also(::startActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingInst = null
    }
}