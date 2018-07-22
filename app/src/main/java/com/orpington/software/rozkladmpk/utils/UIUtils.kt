package com.orpington.software.rozkladmpk.utils

import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Handler
import android.view.View

// https://stackoverflow.com/a/32301440
fun View.forceRippleAnimation() {
    if (Build.VERSION.SDK_INT >= 21 && background is RippleDrawable) {
        val rippleDrawable = background as RippleDrawable

        rippleDrawable.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)

        val handler = Handler()
        handler.postDelayed(Runnable { rippleDrawable.state = intArrayOf() }, 200)
    }
}

