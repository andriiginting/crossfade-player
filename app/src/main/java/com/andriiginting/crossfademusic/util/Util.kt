package com.andriiginting.crossfademusic.util

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import com.andriiginting.crossfademusic.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

fun AppCompatImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(R.color.cardview_dark_background)
        .into(this)
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setTransparentSystemBar() {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun formatTimeInMillisToString(time: Long): String {
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
