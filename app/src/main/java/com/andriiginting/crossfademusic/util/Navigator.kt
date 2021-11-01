package com.andriiginting.crossfademusic.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.andriiginting.crossfademusic.exo.ExoCrossfadeActivity
import com.andriiginting.crossfademusic.mediaplayer.MainActivity
import com.andriiginting.crossfademusic.util.Constant.CROSSFADE_DURATION_EXTRA

object Navigator {

    fun exoPlayerScreen(
        activity: AppCompatActivity,
        crossFadeDuration: Int = 0
    ): Intent {
        return Intent(activity, ExoCrossfadeActivity::class.java)
            .apply {
                putExtra(CROSSFADE_DURATION_EXTRA, crossFadeDuration)
            }
    }

    fun mediaPlayerScreen(
        activity: AppCompatActivity,
        crossFadeDuration: Int = 0
    ): Intent {
        return Intent(activity, MainActivity::class.java)
            .apply {
                putExtra(CROSSFADE_DURATION_EXTRA, crossFadeDuration)
            }
    }
}