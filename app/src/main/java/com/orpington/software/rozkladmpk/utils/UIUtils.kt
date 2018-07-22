package com.orpington.software.rozkladmpk.utils

import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver

// https://stackoverflow.com/a/32301440
fun View.forceRippleAnimation() {
    if (Build.VERSION.SDK_INT >= 21 && background is RippleDrawable) {
        val rippleDrawable = background as RippleDrawable

        rippleDrawable.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)

        val handler = Handler()
        handler.postDelayed(Runnable { rippleDrawable.state = intArrayOf() }, 200)
    }
}

// https://antonioleiva.com/kotlin-ongloballayoutlistener/
inline fun <T: View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

inline fun RecyclerView.whenScrollStateIdle(crossinline f: RecyclerView.() -> Unit) {
    addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                removeOnScrollListener(this)
                f()
            }
        }
    })
}
