package com.andriiginting.crossfademusic.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.andriiginting.crossfademusic.exo.ExoCrossfadeActivity
import com.andriiginting.crossfademusic.mediaplayer.MainActivity

object Navigator {

    fun exoPlayerScreen(activity: AppCompatActivity): Intent {
        return Intent(activity, ExoCrossfadeActivity::class.java)
    }

    fun mediaPlayerScreen(activity: AppCompatActivity): Intent {
        return Intent(activity, MainActivity::class.java)
    }
}