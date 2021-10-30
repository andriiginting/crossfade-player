package com.andriiginting.crossfademusic

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun AppCompatImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(R.color.cardview_dark_background)
        .into(this)
}