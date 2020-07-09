package com.simply.solidwallpaper.ui

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.ImageView

fun ImageView.setBackgroundColorFilter(color: Int) {
    this.background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
}