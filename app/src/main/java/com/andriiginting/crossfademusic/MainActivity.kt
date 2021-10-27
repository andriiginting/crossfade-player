package com.andriiginting.crossfademusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriiginting.crossfademusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var bindingInst: ActivityMainBinding? = null
    private val binding get() = bindingInst!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingInst = null
    }
}