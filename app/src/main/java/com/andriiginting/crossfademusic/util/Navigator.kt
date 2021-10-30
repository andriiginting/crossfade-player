package com.andriiginting.crossfademusic.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.andriiginting.crossfademusic.ExoCrossfadeActivity
import com.andriiginting.crossfademusic.MainActivity

object Navigator {

    fun exoPlayerScreen(activity: AppCompatActivity): Intent {
        return Intent(activity, ExoCrossfadeActivity::class.java)
    }

    fun mediaPlayerScreen(activity: AppCompatActivity): Intent {
        return Intent(activity, MainActivity::class.java)
    }
}